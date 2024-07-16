package de.kontranik.freebudget.database.repository

import android.annotation.SuppressLint
import android.os.Environment
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.dao.CategoryDao
import de.kontranik.freebudget.database.dao.RegularTransactionDao
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.ui.helpers.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RegularTransactionRepository(
    private val mRegularTransactionDao: RegularTransactionDao,
    private val mCategoryDao: CategoryDao
) {

    fun insertRegularTransaction(regularTransaction: RegularTransaction) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            updateCategory(regularTransaction.category)
            mRegularTransactionDao.insertRegularTransaction(regularTransaction)
        }
    }

    fun getTransactionsByMonth(month: Int): Flow<List<RegularTransaction>> {
        return mRegularTransactionDao.getByMonth(month)
    }
    fun getTransactionsByMonthNoLiveData(month: Int): List<RegularTransaction> {
        return mRegularTransactionDao.getByMonthNoLiveData(month)
    }

    fun getById(id: Long?): Flow<RegularTransaction?> {
        return if (id != null) mRegularTransactionDao.getById(id) else flowOf(null)
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
        val trimmedName = categoryName.trim()
        if (trimmedName.isNotEmpty()) {
            val dbCat = mCategoryDao.getByName(trimmedName)
            if (dbCat == null) {
                mCategoryDao.insert(Category(name = trimmedName))
            }
        }
    }

    @SuppressLint("Range")
    fun exportToCSV(baseFileName: String): String {
        val dateFormatLong: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val dateFormatShort: DateFormat = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val outFileName = baseFileName + "_" + dateFormatLong.format(DateUtils.now()) + ".csv"
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
                            clearStringForCSV(description) + Config.CSV_DELIMITER +
                            clearStringForCSV(category) + Config.CSV_DELIMITER +
                            amount.toString() + Config.CSV_DELIMITER +
                            (if (dateStart > 0) dateFormatShort.format(dateStart) else "") + Config.CSV_DELIMITER +
                            (if (dateEnd > 0) dateFormatShort.format(dateEnd) else "") + Config.CSV_DELIMITER +
                            dateFormatLong.format(dateCreate) + Config.CSV_DELIMITER +
                            clearStringForCSV(note ?: "") + Config.CSV_DELIMITER
                            + Config.CSV_NEW_LINE
                        )
                    } while (cursor.moveToNext())
                }
            }
            out.close()
        }
        return fileExport.absolutePath
    }

    fun insertAll(regularTransactionList: MutableList<RegularTransaction>) {
        for (rt in regularTransactionList) {
            insertRegularTransaction(rt)
        }
    }
}