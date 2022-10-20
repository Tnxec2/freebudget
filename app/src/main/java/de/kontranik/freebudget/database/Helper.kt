package de.kontranik.freebudget.database

import android.content.Context
import de.kontranik.freebudget.config.Config

class Helper {
    companion object {
        fun getSortFromSettings(context: Context): String? {
            val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
            val orderBy = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
            val sortDesc = settings.getBoolean(Config.PREF_SORT_DESC, false)
            var sortOrder: String? = null
            if (orderBy != Config.PREF_ORDER_BY_NOT_SORT) {
                sortOrder = orderBy
                if (sortDesc) {
                    sortOrder = "$sortOrder DESC "
                }
            }
            return sortOrder
        }
    }
}