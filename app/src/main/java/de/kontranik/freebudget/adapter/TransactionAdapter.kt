package de.kontranik.freebudget.adapter

import android.content.Context
import android.graphics.Color
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
        val amountFact = String.format(Locale.getDefault(), "%1$,.2f", transaction.amountFact)
        val amountPlanned =
            String.format(Locale.getDefault(), "%1$,.2f", transaction.amountPlanned)
        viewHolder.textviewAmountPlanned.text = amountPlanned
        viewHolder.textviewAmountFact.text = amountFact
        when {
            transaction.amountFact > 0 -> {
                viewHolder.textviewAmountFact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorGreen)
                )
            }
            transaction.amountFact < 0 -> {
                viewHolder.textviewAmountFact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorRed)
                )
            }
            else -> {
                viewHolder.textviewAmountFact.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorTextListItem)
                )
            }
        }
        when {
            transaction.amountPlanned > 0 -> {
                viewHolder.textviewAmountPlanned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorGreen)
                )
            }
            transaction.amountPlanned < 0 -> {
                viewHolder.textviewAmountPlanned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorRed)
                )
            }
            else -> {
                viewHolder.textviewAmountPlanned.setTextColor(
                    ContextCompat.getColor(parent.context, R.color.colorTextListItem)
                )
            }
        }
        viewHolder.descriptionView.text = transaction.description
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        var dateString: String? = parent.context.getString(R.string.not_setted)
        if (transaction.date > 0) {
            dateString = df.format(transaction.date)
        }
        viewHolder.categoryView.text = parent.resources.getString(
            R.string.subTitleTransaction, dateString, transaction.category
        )

        // last edited hervorheben
        if (markLastEdited && transaction.id == AllTransactionFragment.lastEditedId) {
            viewHolder.linearlayoutItem.setBackgroundColor(
                ContextCompat.getColor(
                    parent.context,
                    R.color.colorBackgroundAccent
                )
            )
        } else {
            viewHolder.linearlayoutItem.setBackgroundColor(Color.TRANSPARENT)
        }
        return view!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val linearlayoutItem: LinearLayout = view.findViewById(R.id.linearLayout_Item)
        val textviewAmountPlanned: TextView = view.findViewById(R.id.textView_amount_planned)
        val textviewAmountFact: TextView = view.findViewById(R.id.textView_amount_fact)
        val descriptionView: TextView = view.findViewById(R.id.textView_description)
        val categoryView: TextView = view.findViewById(R.id.textView_category)
    }

    init {
        val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        markLastEdited = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }
}