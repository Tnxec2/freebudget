package de.kontranik.freebudget.config;

import de.kontranik.freebudget.database.DatabaseHelper;

public class Config {
    // Einige Konstante für Einstellungen
    public static final String PREFS_FILE = "Sort";
    public static final String PREF_ORDER_BY = "order_by";
    public static final String PREF_SORT_DESC = "Order_desc";
    public static final String PREF_ORDER_BY_NOT_SORT = "not_sort";
    public static final String PREF_ORDER_BY_DESCRIPTION = DatabaseHelper.COLUMN_DESCRIPTION;
    public static final String PREF_ORDER_BY_AMOUNT = DatabaseHelper.COLUMN_AMOUNT;
    public static final String PREF_ORDER_BY_ABS_AMOUNT = "ABS(" + DatabaseHelper.COLUMN_AMOUNT + ")";
    public static final String PREF_ORDER_BY_YAM = DatabaseHelper.COLUMN_YEAR + ", " + DatabaseHelper.COLUMN_MONTH;
}
