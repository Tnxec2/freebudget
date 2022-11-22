package de.kontranik.freebudget.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.Transaction

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trasaction: Transaction)

    @Update
    fun update(trasaction: Transaction)

    @Query("DELETE FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @get:Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}")
    val getAll: LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} " +
            "where ${DatabaseHelper.COLUMN_DATE} >= :timeStart " +
            "and ${DatabaseHelper.COLUMN_DATE} < :timeEnd " +
            "and ${DatabaseHelper.COLUMN_AMOUNT_FACT} = 0")
    fun getPlannedTransactionsByDate(timeStart: String, timeEnd: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} " +
            "where ${DatabaseHelper.COLUMN_DATE} >= :timeStart " +
            "and ${DatabaseHelper.COLUMN_DATE} < :timeEnd " +
            "and ${DatabaseHelper.COLUMN_AMOUNT_FACT} = 0 " +
            "ORDER BY " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DESCRIPTION}' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DESCRIPTION} DESC' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_CATEGORY_NAME}' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_CATEGORY_NAME} DESC' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_AMOUNT_PLANNED} DESC' THEN ${DatabaseHelper.COLUMN_AMOUNT_PLANNED} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_AMOUNT_PLANNED}' THEN ${DatabaseHelper.COLUMN_AMOUNT_PLANNED} END ASC, " +
            "CASE WHEN :sort_by = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_PLANNED}) DESC' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_PLANNED}) END DESC, " +
            "CASE WHEN :sort_by = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_PLANNED})' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_PLANNED}) END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE}' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE_EDIT} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE_EDIT}' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END ASC"
    )
    fun getPlannedTransactionsByDate(timeStart: String, timeEnd: String, sort_by: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd")
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String): LiveData<List<Transaction>>

    @Query("SELECT * " +
            "FROM ${DatabaseHelper.TABLE_TRANSACTION} " +
            "where ${DatabaseHelper.COLUMN_DATE} >= :timeStart " +
            "and ${DatabaseHelper.COLUMN_DATE} < :timeEnd " +
            "ORDER BY " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DESCRIPTION}' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DESCRIPTION} DESC' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_CATEGORY_NAME}' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_CATEGORY_NAME} DESC' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_AMOUNT_FACT} DESC' THEN ${DatabaseHelper.COLUMN_AMOUNT_FACT} END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_AMOUNT_FACT}' THEN ${DatabaseHelper.COLUMN_AMOUNT_FACT} END ASC, " +
            "CASE WHEN :sort_by = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) DESC' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) END DESC, " +
            "CASE WHEN :sort_by = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT})' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE}' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END ASC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE_EDIT} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END DESC, " +
            "CASE WHEN :sort_by = '${DatabaseHelper.COLUMN_DATE_EDIT}' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END ASC"
    )
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String, sort_by: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd and ${DatabaseHelper.COLUMN_REGULAR_CREATE_DATE} = :regularCreateDate LIMIT 1")
    fun getTransactionsByDateAndRegularCreateDate(timeStart: String, timeEnd: String, regularCreateDate: Long): Transaction?

    @Query("UPDATE ${DatabaseHelper.TABLE_TRANSACTION} SET ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :newName where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name")
    fun updateCategory(name: String, newName: String)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getByID(id: Long): LiveData<Transaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}")
    fun getCursor(): Cursor?

}