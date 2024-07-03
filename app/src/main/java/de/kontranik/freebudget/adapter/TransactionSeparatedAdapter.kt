package de.kontranik.freebudget.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import de.kontranik.freebudget.R
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.model.Transaction
import java.text.DateFormat
import java.util.*

class TransactionSeparatedAdapter(
    context: Context,
    private val transactions: List<Transaction>
) : ArrayAdapter<Transaction?>(context, R.layout.list_view_item_transaction_separated_item, transactions) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val markLastEdited: Boolean
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.list_view_item_transaction_separated_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val transaction = transactions[position]
        val amount = if (transaction.amountFact != 0.0) transaction.amountFact else transaction.amountPlanned

        val amountString = String.format(Locale.getDefault(), "%1$,.2f", amount)

        viewHolder.textviewAmount.text = amountString

        if (transaction.amountFact == 0.0) {
            viewHolder.textviewAmount.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorDarkGray)
            )
        } else {
            viewHolder.textviewAmount.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorTextListItem)
            )
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

        return view!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val linearlayoutItem: LinearLayout = view.findViewById(R.id.linearLayout_Item)
        val textviewAmount: TextView = view.findViewById(R.id.textView_amount)
        val descriptionView: TextView = view.findViewById(R.id.textView_description)
        val categoryView: TextView = view.findViewById(R.id.textView_category)
    }

    init {
        val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        markLastEdited = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }
}