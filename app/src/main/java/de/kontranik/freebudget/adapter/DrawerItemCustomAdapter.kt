package de.kontranik.freebudget.adapter

import de.kontranik.freebudget.model.DrawerItem
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import de.kontranik.freebudget.R
import android.widget.TextView

class DrawerItemCustomAdapter(
    var mContext: Context,
    var layoutResourceId: Int,
    var data: Array<DrawerItem>
) : ArrayAdapter<DrawerItem>(
    mContext, layoutResourceId, data
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        val inflater = (mContext as Activity).layoutInflater
        listItem = inflater.inflate(layoutResourceId, parent, false)
        val imageViewIcon = listItem.findViewById<View>(R.id.imageViewIcon) as ImageView
        val textViewName = listItem.findViewById<View>(R.id.textViewName) as TextView
        val folder = data[position]
        imageViewIcon.setImageResource(folder.icon)
        textViewName.text = folder.name
        return listItem
    }

}