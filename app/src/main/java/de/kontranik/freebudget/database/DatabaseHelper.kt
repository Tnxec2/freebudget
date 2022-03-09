package de.kontranik.freebudget.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + TABLE_CATEGORY +
                    " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT " +
                    ");"
        )
        db.execSQL(
            "CREATE TABLE " + TABLE_REGULAR +
                    " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MONTH + " INTEGER, " +
                    COLUMN_DAY + " DAY, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CATEGORY_NAME + " TEXT, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_DATE_START + " INTEGER, " +
                    COLUMN_DATE_END + " INTEGER, " +
                    COLUMN_DATE_CREATE + " INTEGER " +
                    ");"
        )
        db.execSQL(
            "CREATE TABLE " + TABLE_TRANSACTION +
                    " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ID_REGULAR + " INTEGER," +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CATEGORY_NAME + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_AMOUNT_PLANNED + " REAL, " +
                    COLUMN_AMOUNT_FACT + " REAL, " +
                    COLUMN_DATE_CREATE + " INTEGER ," +
                    COLUMN_DATE_EDIT + " INTEGER " +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                "ALTER TABLE " +
                        TABLE_REGULAR + " ADD COLUMN " + COLUMN_DATE_START + " INTEGER;"
            )
            db.execSQL(
                "ALTER TABLE " +
                        TABLE_REGULAR + " ADD COLUMN " + COLUMN_DATE_END + " INTEGER;"
            )
        }
        /*
            // create old version table
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE +
                    " (" +
                    COLUMN_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_COST + " REAL, " +
                    COLUMN_FREQ + " TEXT, " +
                    COLUMN_YEAR + " INTEGER, " +
                    COLUMN_MONTH + " INTEGER, " +
                    COLUMN_DATE + " INTEGER " +
                    ");");
            // add a column
            db.execSQL("ALTER TABLE " +
                    TABLE + " ADD COLUMN " + COLUMN_TYPE + " TEXT;");
            // set the column value
            db.execSQL("UPDATE " +
                    TABLE + " SET " + COLUMN_TYPE + " = 'r';");
        }
*/
    }

    companion object {
        const val DATABASE_NAME = "freebudget.db"
        private const val SCHEMA = 2 //new database version
        const val TABLE_CATEGORY = "t_category"
        const val TABLE_REGULAR = "t_regular_transaction"
        const val TABLE_TRANSACTION = "t_transaction"
        const val COLUMN_CATEGORY_NAME = "category"

        // column name
        const val COLUMN_ID = "_id"
        const val COLUMN_ID_REGULAR = "id_regular"
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
    }
}