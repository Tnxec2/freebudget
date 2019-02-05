package de.kontranik.freebudget.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "freebudget.db";
    private static final int SCHEMA = 1; //new database version

    static final String TABLE_CATEGORY = "t_category";
    static final String TABLE_REGULAR = "t_regular_transaction";
    static final String TABLE_TRANSACTION = "t_transaction";

    public static final String COLUMN_CATEGORY_NAME = "category";
    // column name
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_REGULAR = "id_regular";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_AMOUNT = "amount";

    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_CREATE_DATE = "create_date";
    public static final String COLUMN_EDIT_DATE = "edit_date";

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT_PLANNED = "amount_planed";
    public static final String COLUMN_AMOUNT_FACT = "amount_fact";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORY +
                " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT " +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_REGULAR +
                " (" +
                COLUMN_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_DAY + " DAY, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY_NAME + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CREATE_DATE + " INTEGER " +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_TRANSACTION +
                " (" +
                COLUMN_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ID_REGULAR   + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY_NAME + " TEXT, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_AMOUNT_PLANNED + " REAL, " +
                COLUMN_AMOUNT_FACT + " REAL, " +
                COLUMN_CREATE_DATE + " INTEGER ," +
                COLUMN_EDIT_DATE + " INTEGER " +
                ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
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
}