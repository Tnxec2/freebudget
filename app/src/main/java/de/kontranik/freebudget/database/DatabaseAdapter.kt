package de.kontranik.freebudget.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.model.Transaction
import java.util.*

@SuppressLint("Range")
class DatabaseAdapter(context: Context) {
    private var order_by: String? = null
    private var sort_desc = false
    private val dbHelper: DatabaseHelper
    private var database: SQLiteDatabase? = null

    fun open(): DatabaseAdapter {
        database = dbHelper.writableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    private fun getAllTransactions(context: Context): Cursor {
        val sortOrder = getSortFromSettings(context)
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ID_REGULAR,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_CATEGORY_NAME,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_AMOUNT_PLANNED,
            DatabaseHelper.COLUMN_AMOUNT_FACT,
            DatabaseHelper.COLUMN_DATE_CREATE,
            DatabaseHelper.COLUMN_DATE_EDIT
        )
        return database!!.query(
            DatabaseHelper.TABLE_TRANSACTION,
            columns,
            null,
            null,
            null,
            null,
            sortOrder
        )
    }


    fun getTransactions(context: Context): List<Transaction> {
        val transactions: MutableList<Transaction> = ArrayList()
        val cursor = getAllTransactions(context)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val idRegular =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR))
                val category =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                val description =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                val amount_planned =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED))
                val amount_fact =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT))
                val date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val date_create =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                val date_edit =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_EDIT))
                transactions.add(
                    Transaction(
                        id,
                        idRegular,
                        description,
                        category,
                        date,
                        amount_planned,
                        amount_fact,
                        date_create,
                        date_edit
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return transactions
    }

    fun getAllPlannedTransactions(context: Context): List<Transaction> {
        val transactions: MutableList<Transaction> = ArrayList()
        val sortOrder = getSortFromSettings(context)
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ID_REGULAR,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_CATEGORY_NAME,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_AMOUNT_PLANNED,
            DatabaseHelper.COLUMN_AMOUNT_FACT,
            DatabaseHelper.COLUMN_DATE_CREATE,
            DatabaseHelper.COLUMN_DATE_EDIT
        )
        val whereClause = DatabaseHelper.COLUMN_AMOUNT_FACT + " = 0 "
        val cursor = database!!.query(
            DatabaseHelper.TABLE_TRANSACTION,
            columns,
            whereClause,
            null,
            null,
            null,
            sortOrder
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val idRegular =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR))
                val category =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                val description =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                val amount_planned =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED))
                val amount_fact =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT))
                val date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val date_create =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                val date_edit =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_EDIT))
                transactions.add(
                    Transaction(
                        id,
                        idRegular,
                        description,
                        category,
                        date,
                        amount_planned,
                        amount_fact,
                        date_create,
                        date_edit
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return transactions
    }

    fun getTransactions(
        context: Context,
        e_year: Int,
        e_month: Int,
        showOnlyPlanned: Boolean
    ): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        val cal: Calendar = GregorianCalendar(e_year, e_month - 1, 1)
        val timeStringStart = cal.timeInMillis.toString()
        cal.add(Calendar.MONTH, 1)
        val timeStringEnd = cal.timeInMillis.toString()
        var whereClause = (DatabaseHelper.COLUMN_DATE + " >= ? "
                + " AND "
                + DatabaseHelper.COLUMN_DATE + " < ? ")
        if (showOnlyPlanned) whereClause += " AND " + DatabaseHelper.COLUMN_AMOUNT_FACT + " = 0 "
        val whereArgs = arrayOf(timeStringStart, timeStringEnd)
        val sortOrder = getSortFromSettings(context)
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ID_REGULAR,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_CATEGORY_NAME,
            DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_AMOUNT_PLANNED,
            DatabaseHelper.COLUMN_AMOUNT_FACT,
            DatabaseHelper.COLUMN_DATE_CREATE,
            DatabaseHelper.COLUMN_DATE_EDIT
        )
        var cursor = database!!.query(
            DatabaseHelper.TABLE_TRANSACTION, columns, whereClause, whereArgs, null, null, sortOrder
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val idRegular =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR))
                val description =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                val category =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                val date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val amount_planned =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED))
                val amount_fact =
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT))
                val create_date =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                val date_edit =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_EDIT))
                transactions.add(
                    Transaction(
                        id,
                        idRegular,
                        description,
                        category,
                        date,
                        amount_planned,
                        amount_fact,
                        create_date,
                        date_edit
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return transactions
    }

    /*
     * prüfen ob reguläre Transaktion bereits in geplanten ist
     */
    fun checkTransactions(e_year: Int, e_month: Int, id_regular: Long): Boolean {
        val cal: Calendar = GregorianCalendar(e_year, e_month - 1, 1)
        val timeStringStart = cal.timeInMillis.toString()
        cal.add(Calendar.MONTH, 1)
        val timeStringEnd = cal.timeInMillis.toString()
        val whereClause = (DatabaseHelper.COLUMN_ID_REGULAR + " = ? "
                + " AND "
                + " ("
                + DatabaseHelper.COLUMN_DATE + " >= ? "
                + " AND "
                + DatabaseHelper.COLUMN_DATE + " < ? "
                + ")")
        val whereArgs = arrayOf(id_regular.toString(), timeStringStart, timeStringEnd)
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID
        )
        var cursor = database!!.query(
            DatabaseHelper.TABLE_TRANSACTION, columns, whereClause, whereArgs, null, null, null
        )
        if (cursor.moveToFirst()) {
            return true
        }
        cursor.close()
        return false
    }

    fun getRegular(month: Int): List<RegularTransaction> {
        val regularTransactions = ArrayList<RegularTransaction>()
        val whereClause = DatabaseHelper.COLUMN_MONTH + " = ?"
        val whereArgs = arrayOf(month.toString())
        val sortOrder = DatabaseHelper.COLUMN_DAY
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_MONTH,
            DatabaseHelper.COLUMN_DAY,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_CATEGORY_NAME,
            DatabaseHelper.COLUMN_AMOUNT,
            DatabaseHelper.COLUMN_DATE_START,
            DatabaseHelper.COLUMN_DATE_END,
            DatabaseHelper.COLUMN_DATE_CREATE
        )
        var cursor = database!!.query(
            DatabaseHelper.TABLE_REGULAR, columns, whereClause, whereArgs, null, null, sortOrder
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val category =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                // int monthDb = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH));
                val day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY))
                val amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT))
                val description =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                val date_create =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                val date_start =
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_START))
                val date_end = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_END))
                regularTransactions.add(
                    RegularTransaction(
                        id,
                        month,
                        day,
                        description,
                        category,
                        amount,
                        date_start,
                        date_end,
                        date_create
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return regularTransactions
    }

    val count: Long
        get() = DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_TRANSACTION)

    fun getTransaction(id: Long): Transaction? {
        var entry: Transaction? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE_TRANSACTION, DatabaseHelper.COLUMN_ID
        )
        val cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val idRegular = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_REGULAR))
            val description =
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
            val category =
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
            val date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
            val amount_planned =
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED))
            val amount_fact =
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT))
            val create_date =
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
            val date_edit = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_EDIT))
            entry = Transaction(
                id,
                idRegular,
                description,
                category,
                date,
                amount_planned,
                amount_fact,
                create_date,
                date_edit
            )
        }
        cursor.close()
        return entry
    }

    fun getRegularById(id: Long): RegularTransaction? {
        var entry: RegularTransaction? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE_REGULAR, DatabaseHelper.COLUMN_ID
        )
        val cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val description =
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
            val category =
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
            val amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT))
            val day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY))
            val month = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH))
            val date_create =
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
            val date_start = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_START))
            val date_end = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_END))
            entry = RegularTransaction(
                id,
                month,
                day,
                description,
                category,
                amount,
                date_start,
                date_end,
                date_create
            )
        }
        cursor.close()
        return entry
    }

    val allRegular: List<RegularTransaction>
        get() {
            val regularTransactions: MutableList<RegularTransaction> = ArrayList()
            val columns = arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_CATEGORY_NAME,
                DatabaseHelper.COLUMN_AMOUNT,
                DatabaseHelper.COLUMN_MONTH,
                DatabaseHelper.COLUMN_DAY,
                DatabaseHelper.COLUMN_DATE_START,
                DatabaseHelper.COLUMN_DATE_END,
                DatabaseHelper.COLUMN_DATE_CREATE
            )
            val cursor = database!!.query(
                DatabaseHelper.TABLE_REGULAR,
                columns,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID
            )
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                    val description =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                    val category =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                    val amount =
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT))
                    val day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY))
                    val month = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH))
                    val date_create =
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                    val date_start =
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_START))
                    val date_end =
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_END))
                    regularTransactions.add(
                        RegularTransaction(
                            id,
                            month,
                            day,
                            description,
                            category,
                            amount,
                            date_start,
                            date_end,
                            date_create
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            return regularTransactions
        }

    fun insert(transaction: Transaction): Long {
        insert(Category(0, transaction.category))
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_ID_REGULAR, transaction.regular_id)
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, transaction.category)
        cv.put(DatabaseHelper.COLUMN_DATE, transaction.date)
        cv.put(DatabaseHelper.COLUMN_AMOUNT_PLANNED, transaction.amount_planned)
        cv.put(DatabaseHelper.COLUMN_AMOUNT_FACT, transaction.amount_fact)
        cv.put(DatabaseHelper.COLUMN_DATE_CREATE, Date().time)
        cv.put(DatabaseHelper.COLUMN_DATE_EDIT, Date().time)
        return database!!.insert(DatabaseHelper.TABLE_TRANSACTION, null, cv)
    }

    fun insert(regularTransaction: RegularTransaction): Long {
        insert(Category(0, regularTransaction.category!!))
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_MONTH, regularTransaction.month)
        cv.put(DatabaseHelper.COLUMN_DAY, regularTransaction.day)
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, regularTransaction.description)
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, regularTransaction.category)
        cv.put(DatabaseHelper.COLUMN_AMOUNT, regularTransaction.amount)
        cv.put(DatabaseHelper.COLUMN_DATE_START, regularTransaction.date_start)
        cv.put(DatabaseHelper.COLUMN_DATE_END, regularTransaction.date_end)
        cv.put(DatabaseHelper.COLUMN_DATE_CREATE, regularTransaction.date_create)
        return database!!.insert(DatabaseHelper.TABLE_REGULAR, null, cv)
    }

    fun deleteTransaction(transactionId: Long): Long {
        val whereClause = DatabaseHelper.COLUMN_ID + " = ?"
        val whereArgs = arrayOf(transactionId.toString())
        return database!!.delete(DatabaseHelper.TABLE_TRANSACTION, whereClause, whereArgs).toLong()
    }

    fun deleteRegularTransaction(transactionId: Long): Long {
        val whereClause = DatabaseHelper.COLUMN_ID + " = ?"
        val whereArgs = arrayOf(transactionId.toString())
        return database!!.delete(DatabaseHelper.TABLE_REGULAR, whereClause, whereArgs).toLong()
    }

    fun update(transaction: Transaction): Long {
        insert(Category(0, transaction.category))
        val whereClause = DatabaseHelper.COLUMN_ID + " = " + transaction.id.toString()
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_ID_REGULAR, transaction.regular_id)
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, transaction.category)
        cv.put(DatabaseHelper.COLUMN_DATE, transaction.date)
        cv.put(DatabaseHelper.COLUMN_AMOUNT_PLANNED, transaction.amount_planned)
        cv.put(DatabaseHelper.COLUMN_AMOUNT_FACT, transaction.amount_fact)
        cv.put(DatabaseHelper.COLUMN_DATE_CREATE, transaction.date_create)
        cv.put(DatabaseHelper.COLUMN_DATE_EDIT, Date().time)
        return database!!.update(DatabaseHelper.TABLE_TRANSACTION, cv, whereClause, null).toLong()
    }

    fun update(regularTransaction: RegularTransaction): Long {
        insert(Category(0, regularTransaction.category!!))
        var whereClause = DatabaseHelper.COLUMN_ID + " = " + regularTransaction.id.toString()
        var cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_MONTH, regularTransaction.month)
        cv.put(DatabaseHelper.COLUMN_DAY, regularTransaction.day)
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, regularTransaction.description)
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, regularTransaction.category)
        cv.put(DatabaseHelper.COLUMN_AMOUNT, regularTransaction.amount)
        cv.put(DatabaseHelper.COLUMN_DATE_START, regularTransaction.date_start)
        cv.put(DatabaseHelper.COLUMN_DATE_END, regularTransaction.date_end)
        cv.put(DatabaseHelper.COLUMN_DATE_CREATE, regularTransaction.date_create)
        val result = database!!.update(DatabaseHelper.TABLE_REGULAR, cv, whereClause, null).toLong()


        /*
         * update auch gleich alle eingeplannte Transactionen
         */whereClause = (DatabaseHelper.COLUMN_ID_REGULAR + " = ? AND "
                + DatabaseHelper.COLUMN_AMOUNT_PLANNED + " = 0 ")
        val whereArgs = arrayOf(regularTransaction.id.toString())
        if (regularTransaction.date_start > 0) {
            whereClause += " AND " + DatabaseHelper.COLUMN_DATE + " >= " + regularTransaction.date_start.toString()
        }
        if (regularTransaction.date_end > 0) {
            whereClause += " AND " + DatabaseHelper.COLUMN_DATE + " <= " + regularTransaction.date_end.toString()
        }
        cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, regularTransaction.description)
        cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, regularTransaction.category)
        cv.put(DatabaseHelper.COLUMN_AMOUNT_PLANNED, regularTransaction.amount)
        database!!.update(DatabaseHelper.TABLE_TRANSACTION, cv, whereClause, whereArgs)
        return result
    }

    val allCategory: List<Category>
        get() {
            val categories: MutableList<Category> = ArrayList()
            val columns = arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_CATEGORY_NAME
            )
            val cursor = database!!.query(
                DatabaseHelper.TABLE_CATEGORY,
                columns,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_CATEGORY_NAME
            )
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                    categories.add(Category(id, name))
                } while (cursor.moveToNext())
            }
            cursor.close()
            return categories
        }

    fun getCategory(id: Long): Category? {
        var entry: Category? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.COLUMN_ID
        )
        val cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
            entry = Category(id, name)
        }
        cursor.close()
        return entry
    }

    fun getCategory(name: String): Category? {
        var entry: Category? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.COLUMN_CATEGORY_NAME
        )
        val cursor = database!!.rawQuery(query, arrayOf(name))
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
            entry = Category(id, name)
        }
        cursor.close()
        return entry
    }

    fun insert(category: Category): Long {
        return if (category.name.trim { it <= ' ' }.length > 0) {
            if (getCategory(category.name) == null) {
                val cv = ContentValues()
                cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.name)
                database!!.insert(DatabaseHelper.TABLE_CATEGORY, null, cv)
            } else {
                0
            }
        } else {
            0
        }
    }

    fun deleteCategory(id: Long): Long {
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(id.toString())
        val category = getCategory(id)
        return if (category != null) {
            val result =
                database!!.delete(DatabaseHelper.TABLE_CATEGORY, whereClause, whereArgs).toLong()
            var query = String.format(
                "UPDATE %s SET %s=? WHERE %s=?",
                DatabaseHelper.TABLE_TRANSACTION,
                DatabaseHelper.COLUMN_CATEGORY_NAME,
                DatabaseHelper.COLUMN_CATEGORY_NAME
            )
            var cursor = database!!.rawQuery(query, arrayOf("", category.name))
            cursor.close()
            query = String.format(
                "UPDATE %s SET %s=? WHERE %s=?",
                DatabaseHelper.TABLE_REGULAR,
                DatabaseHelper.COLUMN_CATEGORY_NAME,
                DatabaseHelper.COLUMN_CATEGORY_NAME
            )
            cursor = database!!.rawQuery(query, arrayOf("", category.name))
            cursor.close()
            result
        } else {
            0
        }
    }

    fun update(category: Category): Long {
        return if (category.name.trim { it <= ' ' }.length > 0) {
            val whereClause = DatabaseHelper.COLUMN_ID + " = " + category.id.toString()
            val cv = ContentValues()
            cv.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.name)
            database!!.update(DatabaseHelper.TABLE_CATEGORY, cv, whereClause, null).toLong()
        } else {
            0
        }
    }

    fun getSortFromSettings(context: Context): String? {
        val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
        order_by = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
        sort_desc = settings.getBoolean(Config.PREF_SORT_DESC, false)
        var sortOrder: String? = null
        if (order_by != Config.PREF_ORDER_BY_NOT_SORT) {
            sortOrder = order_by
            if (sort_desc) {
                sortOrder = "$sortOrder DESC "
            }
        }
        return sortOrder
    }

    init {
        dbHelper = DatabaseHelper(context.applicationContext)
    }
}