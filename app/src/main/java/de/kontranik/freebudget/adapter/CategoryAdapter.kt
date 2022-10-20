package de.kontranik.freebudget.adapter

import android.content.Context
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
    private val inflater: LayoutInflater = LayoutInflater.from(context)
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
        val category = categoryList[position]
        viewHolder.textviewCategoryName.text = category.name
        viewHolder.textviewCategoryWeight.text = String.format("%.2f", category.weight)
        viewHolder.textviewCategoryWeightBackground.text = ""
        val layoutParams =
            viewHolder.textviewCategoryWeightBackground.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width =
            (category.weight * OverviewFragment.maxWidth / OverviewFragment.maxCategoryWeight).toInt()
        viewHolder.textviewCategoryWeightBackground.layoutParams = layoutParams
        return view!!
    }

    private inner class ViewHolder internal constructor(view: View) {
        val textviewCategoryName: TextView = view.findViewById(R.id.textView_CategoryName)
        val textviewCategoryWeight: TextView = view.findViewById(R.id.textView_CategoryWeight)
        val textviewCategoryWeightBackground: TextView = view.findViewById(R.id.textView_CategoryWeightBackground)
    }

}