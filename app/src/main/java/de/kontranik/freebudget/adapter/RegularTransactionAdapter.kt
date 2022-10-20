package de.kontranik.freebudget.adapter

import android.content.Context
import de.kontranik.freebudget.model.RegularTransaction
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.kontranik.freebudget.R
import androidx.core.content.ContextCompat
import android.widget.TextView
import java.text.DateFormat
import java.util.*

class RegularTransactionAdapter(
    context: Context?,
    private val layout: Int,
    private val regularTransactions: MutableList<RegularTransaction>
) : ArrayAdapter<RegularTransaction>(
    context!!, layout, regularTransactions
) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    fun updateTransactionsList(newlist: List<RegularTransaction>?) {
        regularTransactions.clear()
        regularTransactions.addAll(newlist!!)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            view =
                inflater.inflate(R.layout.list_view_item_regular_transaction_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val regularTransaction = regularTransactions[position]
        viewHolder.descriptionView.text = regularTransaction.description
        viewHolder.amountView.text =
            String.format(Locale.getDefault(), "%1$,.2f", regularTransaction.amount)
        if (regularTransaction.amount > 0) {
            viewHolder.amountView.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorGreen)
            )
        } else {
            viewHolder.amountView.setTextColor(
                ContextCompat.getColor(parent.context, R.color.colorRed)
            )
        }
        var text = parent.resources.getString(
            R.string.subTitleTransaction,
            java.lang.String.valueOf(regularTransaction.day),
            regularTransaction.category
        )
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        //SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy H:mm:ss S");
        if (regularTransaction.dateStart != null) text += ", " + context.getString(R.string.start) + ": " + df.format(
            regularTransaction.dateStart
        )
        if (regularTransaction.dateEnd != null) text += ", " + context.getString(R.string.end) + ": " + df.format(
            regularTransaction.dateEnd
        )
        viewHolder.categoryView.text = text
        return view!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val amountView: TextView = view.findViewById(R.id.textView_amount_regular)
        val descriptionView: TextView = view.findViewById(R.id.textView_description_regular)
        val categoryView: TextView = view.findViewById(R.id.textView_category_regular)
    }

}