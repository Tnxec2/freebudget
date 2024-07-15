package de.kontranik.freebudget.database.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.Helper
import de.kontranik.freebudget.database.dao.CategoryDao
import de.kontranik.freebudget.database.dao.TransactionDao
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TransactionRepository(
    private val mTransactionDao: TransactionDao,
    private val mCategoryDao: CategoryDao,
    private val context: Context
    ) {

    fun checkPoint() {
        // to ensure all of the pending transactions are applied
        // https://androidexplained.github.io/android/room/2020/10/03/room-backup-restore.html#h-restore-database
        mTransactionDao.checkpoint((SimpleSQLiteQuery("pragma wal_checkpoint(full)")))
    }


    fun insert(transaction: Transaction) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            updateCategory(transaction.category)
            mTransactionDao.insert(transaction)
        }
    }

    fun getTransactions(eYear: Int,
                        eMonth: Int): Flow<List<Transaction>> {
        val sortOrder = Helper.getSortFromSettings(context)
        val cal: Calendar = GregorianCalendar(eYear, eMonth - 1, 1)
        val timeStringStart = cal.timeInMillis.toString()
        cal.add(Calendar.MONTH, 1)
        val timeStringEnd = cal.timeInMillis.toString()
        sortOrder?.let { Log.d("getTransactions", it) }
        return if (sortOrder == null)
                mTransactionDao.getAllTransactionsByDate(timeStringStart, timeStringEnd)
            else
                mTransactionDao.getAllTransactionsByDate(timeStringStart, timeStringEnd, sortOrder)
    }

    fun getTransactionByRegularCreateDate(e_year: Int, e_month: Int, regularCreateDate: Long): Transaction? {
        val cal: Calendar = GregorianCalendar(e_year, e_month - 1, 1)
        val timeStringStart = cal.timeInMillis.toString()
        cal.add(Calendar.MONTH, 1)
        val timeStringEnd = cal.timeInMillis.toString()
        return mTransactionDao.getTransactionsByDateAndRegularCreateDate(timeStringStart, timeStringEnd, regularCreateDate)
    }

    fun getTransactionByID(id: Long): Flow<Transaction> {
        return mTransactionDao.getByID(id)
    }

    fun delete(id: Long) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            mTransactionDao.delete(id)
        }
    }

    fun update(transaction: Transaction) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            updateCategory(transaction.category)
            mTransactionDao.update(transaction)
        }
    }

    @SuppressLint("Range")
    fun exportToCSV(fileName: String): String {
        val dfMillis: DateFormat = SimpleDateFormat(Config.DATE_MILLIS, Locale.US)
        val dfLong: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val dfShort: DateFormat = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val outFileName = fileName + "_" + dfLong.format(Date()) + ".csv"
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileExport = File(directory, outFileName)
        val out = FileWriter(fileExport)
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            val cursor = mTransactionDao.getCursor()
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                        val category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                        val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                        val note = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE))
                        val amountPlanned = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_PLANNED))
                        val amountFact = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT_FACT))
                        val date = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                        val dateCreate = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                        val dateEdit = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_EDIT))
                        val regularCreateDate = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_REGULAR_CREATE_DATE))
                        out.append(
                            id.toString() + Config.CSV_DELIMITER +
                                    clearStringForCSV(description) + Config.CSV_DELIMITER +
                                    clearStringForCSV(category ?: "") + Config.CSV_DELIMITER +
                                    dfShort.format(date) + Config.CSV_DELIMITER +
                                    amountPlanned.toString() + Config.CSV_DELIMITER +
                                    amountFact.toString() + Config.CSV_DELIMITER +
                                    dfLong.format(dateCreate) + Config.CSV_DELIMITER +
                                    dfLong.format(dateEdit) + Config.CSV_DELIMITER +
                                    clearStringForCSV(note ?: "") + Config.CSV_DELIMITER +
                                    (if (regularCreateDate == 0L) "" else regularCreateDate.toString()) + Config.CSV_DELIMITER
                                    + Config.CSV_NEW_LINE
                        )
                    } while (cursor.moveToNext())
                }
            }
            out.close()
        }
        return fileExport.absolutePath
    }

    private fun updateCategory(categoryName: String) {
        val trimmedName = categoryName.trim()
        if (trimmedName.isNotEmpty()) {
            val dbCat = mCategoryDao.getByName(trimmedName)
            if (dbCat == null) {
                mCategoryDao.insert(Category(name = trimmedName))
            }
        }
    }

    fun insertAll(transactionList: MutableList<Transaction>) {
        for (transaction in transactionList) {
            insert(transaction)
        }
    }
}

fun clearStringForCSV(s: String): String {
    return s.replace(Config.CSV_DELIMITER, Config.CSV_DELIMITER_REPLACER)
}