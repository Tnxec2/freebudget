package de.kontranik.freebudget.database.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.Helper
import de.kontranik.freebudget.database.dao.CategoryDao
import de.kontranik.freebudget.database.dao.RegularTransactionDao
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


internal class RegularTransactionRepository(var context: Context) {
    private val mRegularTransactionDao: RegularTransactionDao
    private val mCategoryDao: CategoryDao

    init {
        val db: FreeBudgetRoomDatabase = FreeBudgetRoomDatabase.getDatabase(context)
        mRegularTransactionDao = db.regularTransactionDao()
        mCategoryDao = db.categoryDao()
    }

    fun insertRegularTransaction(regularTransaction: RegularTransaction) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            updateCategory(regularTransaction.category)
            mRegularTransactionDao.insertRegularTransaction(regularTransaction)
        }
    }

    fun getAllLiveData(): LiveData<List<RegularTransaction>> {
        val sortOrder = Helper.getSortFromSettings(context)
        return if (sortOrder==null) mRegularTransactionDao.getAllLiveData() else mRegularTransactionDao.getAllLiveData(sortOrder)
    }

    fun getAll(): List<RegularTransaction> {
        val sortOrder = Helper.getSortFromSettings(context)
        return if (sortOrder==null) mRegularTransactionDao.getAll() else mRegularTransactionDao.getAll(sortOrder)
    }

    fun getTransactionsByMonth(month: Int): LiveData<List<RegularTransaction>> {
        return mRegularTransactionDao.getByMonth(month)
    }
    fun getTransactionsByMonthNoLiveData(month: Int): List<RegularTransaction> {
        return mRegularTransactionDao.getByMonthNoLiveData(month)
    }

    fun getById(id: Long): LiveData<RegularTransaction> {
        return mRegularTransactionDao.getById(id)
    }

    fun delete(id: Long) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            mRegularTransactionDao.delete(id)
        }
    }

    fun update(regularTransaction: RegularTransaction) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            updateCategory(regularTransaction.category)
            mRegularTransactionDao.update(regularTransaction)
        }
    }

    private fun updateCategory(categoryName: String) {
        if (categoryName.trim().isNotEmpty()) {
            val dbCat = mCategoryDao.getByName(categoryName)
            if (dbCat == null) {
                mCategoryDao.insert(Category(name = categoryName))
            }
        }
    }

    @SuppressLint("Range")
    fun exportToCSV(baseFileName: String): String {
        val dateFormatLong: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val dateFormatShort: DateFormat = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val outFileName = baseFileName + "_" + dateFormatLong.format(Date()) + ".csv"
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileExport = File(directory, outFileName)
        val out = FileWriter(fileExport)
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            val cursor = mRegularTransactionDao.getCursor()
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val month =
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MONTH))
                        val day = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DAY))
                        val category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME))
                        val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION))
                        val note = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE))
                        val amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT))
                        val dateStart = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_START))
                        val dateEnd = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_END))
                        val dateCreate = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_CREATE))
                        out.append(
                            month + Config.CSV_DELIMITER +
                            day + Config.CSV_DELIMITER +
                            description + Config.CSV_DELIMITER +
                            category + Config.CSV_DELIMITER +
                            amount.toString() + Config.CSV_DELIMITER +
                            (if (dateStart > 0) dateFormatShort.format(dateStart) else "") + Config.CSV_DELIMITER +
                            (if (dateEnd > 0) dateFormatShort.format(dateEnd) else "") + Config.CSV_DELIMITER +
                            dateFormatLong.format(dateCreate) + Config.CSV_DELIMITER +
                            (note ?: "") + Config.CSV_DELIMITER
                            + Config.CSV_NEW_LINE
                        )
                    } while (cursor.moveToNext())
                }
            }
            out.close()
        }
        return fileExport.absolutePath
    }
}