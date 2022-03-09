package de.kontranik.freebudget.adapter

import android.content.Context
import de.kontranik.freebudget.model.Transaction.amount_fact
import de.kontranik.freebudget.model.Transaction.amount_planned
import de.kontranik.freebudget.model.Transaction.description
import de.kontranik.freebudget.model.Transaction.date
import de.kontranik.freebudget.model.Transaction.category
import de.kontranik.freebudget.model.Transaction.id
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import de.kontranik.freebudget.R
import de.kontranik.freebudget.fragment.AllTransactionFragment
import android.widget.LinearLayout
import android.widget.TextView
import android.content.SharedPreferences
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
    private val inflater: LayoutInflater
    private val markLastEdited: Boolean
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val transaction = transactions[position]
        val amount_fact = String.format(Locale.getDefault(), "%1$,.2f", transaction.amount_fact)
        val amount_planned =
            String.format(Locale.getDefault(), "%1$,.2f", transaction.amount_planned)
        viewHolder.textView_amount_planned.text = amount_planned
        viewHolder.textView_amount_fact.text = amount_fact
        if (transaction.amount_fact > 0) {
            viewHolder.textView_amount_fact.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorGreen)
            )
        } else if (transaction.amount_fact < 0) {
            viewHolder.textView_amount_fact.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorRed)
            )
        } else {
            viewHolder.textView_amount_fact.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorBlack)
            )
        }
        if (transaction.amount_planned > 0) {
            viewHolder.textView_amount_planned.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorGreen)
            )
        } else if (transaction.amount_planned < 0) {
            viewHolder.textView_amount_planned.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorRed)
            )
        } else {
            viewHolder.textView_amount_planned.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorBlack)
            )
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
        return convertView!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val linearLayout_Item: LinearLayout
        val textView_amount_planned: TextView
        val textView_amount_fact: TextView
        val descriptionView: TextView
        val categoryView: TextView

        init {
            linearLayout_Item = view.findViewById(R.id.linearLayout_Item)
            textView_amount_planned = view.findViewById(R.id.textView_amount_planned)
            textView_amount_fact = view.findViewById(R.id.textView_amount_fact)
            descriptionView = view.findViewById(R.id.textView_description)
            categoryView = view.findViewById(R.id.textView_category)
        }
    }

    init {
        inflater = LayoutInflater.from(context)
        val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        markLastEdited = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }
}