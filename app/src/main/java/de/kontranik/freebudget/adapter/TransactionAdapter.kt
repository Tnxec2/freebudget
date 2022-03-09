package de.kontranik.freebudget.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import de.kontranik.freebudget.R
import de.kontranik.freebudget.fragment.AllTransactionFragment
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.model.Transaction
import java.text.DateFormat
import java.util.*

class TransactionAdapter(
    context: Context,
    private val layout: Int,
    private val transactions: List<Transaction>
) : ArrayAdapter<Transaction?>(context, layout, transactions) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val markLastEdited: Boolean
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = inflater.inflate(layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val transaction = transactions[position]
        val amount_fact = String.format(Locale.getDefault(), "%1$,.2f", transaction.amount_fact)
        val amount_planned =
            String.format(Locale.getDefault(), "%1$,.2f", transaction.amount_planned)
        viewHolder.textView_amount_planned.text = amount_planned
        viewHolder.textView_amount_fact.text = amount_fact
        when {
            transaction.amount_fact > 0 -> {
                viewHolder.textView_amount_fact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorGreen)
                )
            }
            transaction.amount_fact < 0 -> {
                viewHolder.textView_amount_fact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorRed)
                )
            }
            else -> {
                viewHolder.textView_amount_fact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorBlack)
                )
            }
        }
        when {
            transaction.amount_planned > 0 -> {
                viewHolder.textView_amount_planned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorGreen)
                )
            }
            transaction.amount_planned < 0 -> {
                viewHolder.textView_amount_planned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorRed)
                )
            }
            else -> {
                viewHolder.textView_amount_planned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorBlack)
                )
            }
        }
        viewHolder.descriptionView.text = transaction.description
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        var dateString: String? = parent.context.getString(R.string.not_set)
        if (transaction.date > 0) {
            dateString = df.format(transaction.date)
        }
        viewHolder.categoryView.text = parent.resources.getString(
            R.string.subTitleTransaction, dateString, transaction.category
        )

        // last edited hervorheben
        if (markLastEdited && transaction.id == AllTransactionFragment.lastEditedId) {
            viewHolder.descriptionView.setTextColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorBackgroundListItem
                )
            )
            viewHolder.categoryView.setTextColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorBackgroundListItem
                )
            )
            viewHolder.linearLayout_Item.setBackgroundColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorBackgroundAccent
                )
            )
        } else {
            viewHolder.descriptionView.setTextColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorTextListItem
                )
            )
            viewHolder.categoryView.setTextColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorTextListItem
                )
            )
            viewHolder.linearLayout_Item.setBackgroundColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorBackgroundListItem
                )
            )
        }
        return view!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val linearLayout_Item: LinearLayout = view.findViewById(R.id.linearLayout_Item)
        val textView_amount_planned: TextView = view.findViewById(R.id.textView_amount_planned)
        val textView_amount_fact: TextView = view.findViewById(R.id.textView_amount_fact)
        val descriptionView: TextView = view.findViewById(R.id.textView_description)
        val categoryView: TextView = view.findViewById(R.id.textView_category)

    }

    init {
        val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        markLastEdited = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }
}