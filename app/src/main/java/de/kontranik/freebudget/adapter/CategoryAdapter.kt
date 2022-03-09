package de.kontranik.freebudget.adapter

import android.content.Context
import de.kontranik.freebudget.model.Category.name
import de.kontranik.freebudget.model.Category.weight
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import de.kontranik.freebudget.fragment.OverviewFragment
import android.widget.TextView
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Category

class CategoryAdapter(
    context: Context?,
    private val layout: Int,
    private val categoryList: List<Category>
) : ArrayAdapter<Category?>(
    context!!, layout, categoryList
) {
    private val inflater: LayoutInflater
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
        val category = categoryList[position]
        viewHolder.textView_CategoryName.text = category.name
        viewHolder.textView_CategoryWeight.text = String.format("%.2f", category.weight)
        viewHolder.textView_CategoryWeightBackground.text = ""
        val layoutParams =
            viewHolder.textView_CategoryWeightBackground.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width =
            (category.weight * OverviewFragment.maxWidth / OverviewFragment.maxCategoryWeight).toInt()
        viewHolder.textView_CategoryWeightBackground.layoutParams = layoutParams
        return convertView!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val textView_CategoryName: TextView
        val textView_CategoryWeight: TextView
        val textView_CategoryWeightBackground: TextView

        init {
            textView_CategoryName = view.findViewById(R.id.textView_CategoryName)
            textView_CategoryWeight = view.findViewById(R.id.textView_CategoryWeight)
            textView_CategoryWeightBackground =
                view.findViewById(R.id.textView_CategoryWeightBackground)
        }
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}