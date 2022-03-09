package de.kontranik.freebudget.config

import de.kontranik.freebudget.database.DatabaseHelper

object Config {
    // Einige Konstante für Einstellungen
    const val DATE_LONG = "dd-MM-yyyy_HH-mm-ss"
    const val DATE_SHORT = "dd-MM-yyyy"
    const val CSV_DELIMITER = ";"
    const val CSV_NEW_LINE = "\n"
    const val CSV_CODE_PAGE = "UTF-8"
    const val PREFS_FILE = "Sort"
    const val PREF_ORDER_BY = "order_by"
    const val PREF_SORT_DESC = "Order_desc"
    const val PREF_ORDER_BY_NOT_SORT = "not_sort"
    const val PREF_ORDER_BY_DESCRIPTION = DatabaseHelper.COLUMN_DESCRIPTION
    const val PREF_ORDER_BY_CATEGORY_NAME = DatabaseHelper.COLUMN_CATEGORY_NAME
    const val PREF_ORDER_BY_AMOUNT = DatabaseHelper.COLUMN_AMOUNT
    const val PREF_ORDER_BY_ABS_AMOUNT = "ABS(" + DatabaseHelper.COLUMN_AMOUNT + ")"
    const val PREF_ORDER_BY_EDIT_DATE = DatabaseHelper.COLUMN_DATE_EDIT
    const val PREF_ORDER_BY_DATE = DatabaseHelper.COLUMN_DATE
    const val PREF_MARK_LAST_EDITED = "mark_last_edited"
}