package de.kontranik.freebudget.config;

import de.kontranik.freebudget.database.DatabaseHelper;

public class Config {
    // Einige Konstante für Einstellungen
    public static final String DATE_LONG = "dd-MM-yyyy_HH-mm-ss";
    public static final String DATE_SHORT = "dd-MM-yyyy";
    public static final String CSV_DELIMITER = ";";
    public static final String CSV_NEW_LINE = "\n";
    public static final String CSV_CODE_PAGE = "UTF-8";
    public static final String PREFS_FILE = "Sort";
    public static final String PREF_ORDER_BY = "order_by";
    public static final String PREF_SORT_DESC = "Order_desc";
    public static final String PREF_ORDER_BY_NOT_SORT = "not_sort";
    public static final String PREF_ORDER_BY_DESCRIPTION = DatabaseHelper.COLUMN_DESCRIPTION;
    public static final String PREF_ORDER_BY_AMOUNT = DatabaseHelper.COLUMN_AMOUNT;
    public static final String PREF_ORDER_BY_ABS_AMOUNT = "ABS(" + DatabaseHelper.COLUMN_AMOUNT + ")";
    public static final String PREF_ORDER_BY_YAM = DatabaseHelper.COLUMN_YEAR + ", " + DatabaseHelper.COLUMN_MONTH;
}
