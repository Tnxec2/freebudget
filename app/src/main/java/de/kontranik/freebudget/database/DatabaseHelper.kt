package de.kontranik.freebudget.database


class DatabaseHelper {

    companion object {
        const val DATABASE_NAME = "freebudget.db"
        const val TABLE_CATEGORY = "t_category"
        const val TABLE_REGULAR = "t_regular_transaction"
        const val TABLE_TRANSACTION = "t_transaction"
        const val COLUMN_CATEGORY_NAME = "category"

        // column name
        const val COLUMN_ID = "_id"
        const val COLUMN_REGULAR_CREATE_DATE = "regular_create_date"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_MONTH = "month"
        const val COLUMN_DAY = "day"
        const val COLUMN_DATE_CREATE = "create_date"
        const val COLUMN_DATE_EDIT = "edit_date"
        const val COLUMN_DATE_START = "start_date"
        const val COLUMN_DATE_END = "end_date"
        const val COLUMN_DATE = "date"
        const val COLUMN_AMOUNT_PLANNED = "amount_planned"
        const val COLUMN_AMOUNT_FACT = "amount_fact"
        const val COLUMN_NOTE = "note"
    }
}