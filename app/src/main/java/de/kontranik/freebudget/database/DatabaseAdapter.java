package de.kontranik.freebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.kontranik.freebudget.config.Config;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.RegularTransaction;
import de.kontranik.freebudget.model.Transaction;

public class DatabaseAdapter {
    private SharedPreferences settings;
    private String order_by;
    private Boolean sort_desc = false;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;


    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllTransactions(Context context){
        settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE);
        order_by = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT);
        sort_desc = settings.getBoolean(Config.PREF_SORT_DESC, false);

        String sortOrder = null;
        if (!order_by.equals(Config.PREF_ORDER_BY_NOT_SORT)) {
            sortOrder = order_by;

            if (sort_desc) {
                sortOrder = sortOrder + " DESC ";
            }
        }

        String[] columns = new String[] {   DatabaseHelper.COLUMN_ID,
                                            DatabaseHelper.COLUMN_ID_REGULAR,
                                            DatabaseHelper.COLUMN_DESCRIPTION,
                                            DatabaseHelper.COLUMN_CATEGORY_NAME,
                                            DatabaseHelper.COLUMN_DATE_PLANNED,
                                            DatabaseHelper.COLUMN_DATE_FACT,
                                            DatabaseHelper.COLUMN_AMOUNT_PLANNED,
                                            DatabaseHelper.COLUMN_AMOUNT_FACT,
                                            DatabaseHelper.COLUMN_CREATE_DATE
                                        };
        return  database.query(DatabaseHelper.TABLE_TRANSACTION, columns, null, null, null, null, sortOrder);
    }


    public List<Transaction> getTransactions(Context context){
        List<Transaction> transactions = new ArrayList<>();

        Cursor cursor = getAllTransactions(context);
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                long idRegular = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR));
                String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                double amount_planed = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED));
                double amount_fact = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT));
                long date_planed = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_PLANNED));
                long date_fact = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_FACT));
                long date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_DATE));
                transactions.add(new Transaction(id, idRegular, description, category, date_planed, date_fact, amount_planed, amount_fact, date));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return  transactions;
    }

    public List<Transaction> getTransactions(int e_year, int e_month){

        ArrayList<Transaction> transactions = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(e_year, e_month , 1);
        cal.add(Calendar.MONTH,1);

        String timeString = String.valueOf( cal.getTimeInMillis() );

        String whereClause = DatabaseHelper.COLUMN_DATE_PLANNED + " IS NOT NULL AND " + DatabaseHelper.COLUMN_DATE_PLANNED + " < ? OR "
                + DatabaseHelper.COLUMN_DATE_FACT + " IS NOT NULL AND "  + DatabaseHelper.COLUMN_DATE_FACT + " < ?";

        String[] whereArgs = new String[]{ timeString, timeString };

        String sortOrder = DatabaseHelper.COLUMN_DATE_PLANNED;

        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_ID_REGULAR,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_CATEGORY_NAME,
                DatabaseHelper.COLUMN_DATE_PLANNED,
                DatabaseHelper.COLUMN_DATE_FACT,
                DatabaseHelper.COLUMN_AMOUNT_PLANNED,
                DatabaseHelper.COLUMN_AMOUNT_FACT,
                DatabaseHelper.COLUMN_CREATE_DATE
        };
        this.cursor = database.query(
                DatabaseHelper.TABLE_TRANSACTION, columns, whereClause, whereArgs, null, null, sortOrder);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                long idRegular = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
                long date_planed = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_PLANNED));
                long date_fact = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_FACT));
                double amount_planed = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED));
                double amount_fact = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT));
                long create_date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_DATE));
                transactions.add( new Transaction(id, idRegular, description, category, date_planed, date_fact, amount_planed, amount_fact, create_date) );
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return  transactions;
    }

    public List<RegularTransaction> getTransactions(int month){

        ArrayList<RegularTransaction> regularTransactions = new ArrayList<>();

        String whereClause = DatabaseHelper.COLUMN_MONTH + " = ? OR " + DatabaseHelper.COLUMN_MONTH + " = 0";
        String[] whereArgs = new String[]{String.valueOf(month)};

        String sortOrder = DatabaseHelper.COLUMN_DAY;

        String[] columns = new String[] {   DatabaseHelper.COLUMN_ID,
                                            DatabaseHelper.COLUMN_MONTH,
                                            DatabaseHelper.COLUMN_DAY,
                                            DatabaseHelper.COLUMN_DESCRIPTION,
                                            DatabaseHelper.COLUMN_CATEGORY_NAME,
                                            DatabaseHelper.COLUMN_AMOUNT,
                                            DatabaseHelper.COLUMN_CREATE_DATE
                                        };
         this.cursor = database.query(
                 DatabaseHelper.TABLE_REGULAR, columns, whereClause, whereArgs, null, null, sortOrder);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
                int monthDb = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH));
                int day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY));
                double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                long date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_DATE));
                regularTransactions.add(new RegularTransaction(id, month, day, description, category, amount, date));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return  regularTransactions;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_TRANSACTION);
    }

    public Transaction getTransaction(long id){
        Transaction entry = null;

        String query = String.format("SELECT * FROM %s WHERE %s=?",
                                    DatabaseHelper.TABLE_TRANSACTION, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            long idRegular = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
            String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
            long date_planed = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_PLANNED));
            long date_fact = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_FACT));
            double amount_planed = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED));
            double amount_fact = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT));
            long create_date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_DATE));
            entry = new Transaction(id, idRegular, description, category, date_planed, date_fact, amount_planed, amount_fact, create_date);
        }
        cursor.close();

        return  entry;
    }

    public RegularTransaction getRegularTransaction(long id){
        RegularTransaction entry = null;

        String query = String.format("SELECT * FROM %s WHERE %s=?",
                                    DatabaseHelper.TABLE_REGULAR, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
            String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
            double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
            int day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY));
            int month = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATE_DATE));
            entry = new RegularTransaction(id, month, day, description, category, amount, date);
        }
        cursor.close();

        return  entry;
    }

    public long insert(Transaction transaction){
        insert(new Category(0, transaction.getCategory()));
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_ID_REGULAR, transaction.getRegular_id());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, transaction.getCategory());
        cv.put(DatabaseHelper.COLUMN_DATE_PLANNED, transaction.getDate_planed());
        cv.put(DatabaseHelper.COLUMN_DATE_FACT, transaction.getDate_fact());
        cv.put(DatabaseHelper.COLUMN_AMOUNT_PLANNED, transaction.getAmount_planed());
        cv.put(DatabaseHelper.COLUMN_AMOUNT_FACT, transaction.getAmount_fact());
        cv.put(DatabaseHelper.COLUMN_CREATE_DATE, transaction.getDate_create());
        return  database.insert(DatabaseHelper.TABLE_TRANSACTION, null, cv);
    }

    public long insert(RegularTransaction regularTransaction){
        insert(new Category(0, regularTransaction.getCategory()));
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_MONTH, regularTransaction.getMonth());
        cv.put(DatabaseHelper.COLUMN_DAY, regularTransaction.getDay());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, regularTransaction.getDescription());
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, regularTransaction.getCategory());
        cv.put(DatabaseHelper.COLUMN_AMOUNT, regularTransaction.getAmount());
        cv.put(DatabaseHelper.COLUMN_CREATE_DATE, regularTransaction.getDate_create());
        return  database.insert(DatabaseHelper.TABLE_REGULAR, null, cv);
    }

    public long deleteTransaction(long transactionId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(transactionId)};

        return database.delete(DatabaseHelper.TABLE_TRANSACTION, whereClause, whereArgs);
    }

    public long deleteRegularTransaction(long transactionId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(transactionId)};

        return database.delete(DatabaseHelper.TABLE_REGULAR, whereClause, whereArgs);
    }


    public long update(Transaction transaction){

        insert(new Category(0, transaction.getCategory()));

        String whereClause = DatabaseHelper.COLUMN_ID + " = " +
                                String.valueOf(transaction.getId());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_ID_REGULAR, transaction.getRegular_id());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, transaction.getCategory());
        cv.put(DatabaseHelper.COLUMN_DATE_PLANNED, transaction.getDate_planed());
        cv.put(DatabaseHelper.COLUMN_DATE_FACT, transaction.getDate_fact());
        cv.put(DatabaseHelper.COLUMN_AMOUNT_PLANNED, transaction.getAmount_planed());
        cv.put(DatabaseHelper.COLUMN_AMOUNT_FACT, transaction.getAmount_fact());
        cv.put(DatabaseHelper.COLUMN_CREATE_DATE, transaction.getDate_create());
        return database.update(DatabaseHelper.TABLE_TRANSACTION, cv, whereClause, null);
    }

    public long update(RegularTransaction regularTransaction){
        insert(new Category(0, regularTransaction.getCategory()));

        String whereClause = DatabaseHelper.COLUMN_ID + " = " +
                String.valueOf(regularTransaction.getId());

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_MONTH, regularTransaction.getMonth());
        cv.put(DatabaseHelper.COLUMN_DAY, regularTransaction.getDay());
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, regularTransaction.getDescription());
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, regularTransaction.getCategory());
        cv.put(DatabaseHelper.COLUMN_AMOUNT, regularTransaction.getAmount());
        cv.put(DatabaseHelper.COLUMN_CREATE_DATE, regularTransaction.getDate_create());
        return database.update(DatabaseHelper.TABLE_TRANSACTION, cv, whereClause, null);
    }

    public List<Category> getAllCategory(){
        List<Category> categories = new ArrayList<Category>();

        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_CATEGORY_NAME
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_CATEGORY, columns, null, null, null, null, DatabaseHelper.COLUMN_CATEGORY_NAME);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));

                categories.add(new Category(id, name));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return  categories;
    }

    public Category getCategory(long id){
        Category entry = null;

        String query = String.format("SELECT * FROM %s WHERE %s=?",
                DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery( query, new String[]{ String.valueOf(id) } );
        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
            entry = new Category(id, name);
        }
        cursor.close();

        return  entry;
    }

    public Category getCategory(String name){
        Category entry = null;

        String query = String.format("SELECT * FROM %s WHERE %s=?",
                DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.COLUMN_CATEGORY_NAME);
        Cursor cursor = database.rawQuery( query, new String[]{ name } );
        if(cursor.moveToFirst()){
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            entry = new Category(id, name);
        }
        cursor.close();

        return  entry;
    }

    public long insert(Category category) {
        if ( getCategory(category.getName()) == null) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
            return database.insert(DatabaseHelper.TABLE_CATEGORY, null, cv);
        } else {
            return 0;
        }
    }

    public long deleteCategory(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        Category category = getCategory(id);
        if ( category != null) {
            long result = database.delete(DatabaseHelper.TABLE_CATEGORY, whereClause, whereArgs);

            String query = String.format("UPDATE %s SET %s=? WHERE %s=?",
                    DatabaseHelper.TABLE_TRANSACTION, DatabaseHelper.COLUMN_CATEGORY_NAME, DatabaseHelper.COLUMN_CATEGORY_NAME);
            Cursor cursor = database.rawQuery(query, new String[]{"", category.getName()});
            cursor.close();

            query = String.format("UPDATE %s SET %s=? WHERE %s=?",
                    DatabaseHelper.TABLE_REGULAR, DatabaseHelper.COLUMN_CATEGORY_NAME, DatabaseHelper.COLUMN_CATEGORY_NAME);
            cursor = database.rawQuery(query, new String[]{"", category.getName()});
            cursor.close();
            return result;
        } else {
            return 0;
        }
    }

    public long update(Category category){
        String whereClause = DatabaseHelper.COLUMN_ID + " = " +
                String.valueOf(category.getId());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        return database.update(DatabaseHelper.TABLE_CATEGORY, cv, whereClause, null);
    }

}