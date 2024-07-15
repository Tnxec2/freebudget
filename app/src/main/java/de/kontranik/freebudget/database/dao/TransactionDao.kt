package de.kontranik.freebudget.database.dao

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trasaction: Transaction)

    @Update
    fun update(trasaction: Transaction)

    @Query("DELETE FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @get:Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}")
    val getAll: Flow<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where " +
            "${DatabaseHelper.COLUMN_DATE} >= :timeStart " +
            "and ${DatabaseHelper.COLUMN_DATE} < :timeEnd " +
            "")
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String): Flow<List<Transaction>>

    @Query("SELECT * " +
            "FROM ${DatabaseHelper.TABLE_TRANSACTION} " +
            "where ${DatabaseHelper.COLUMN_DATE} >= :timeStart " +
            "and ${DatabaseHelper.COLUMN_DATE} < :timeEnd " +
            "ORDER BY " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DESCRIPTION}' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END ASC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DESCRIPTION} DESC' THEN ${DatabaseHelper.COLUMN_DESCRIPTION} END DESC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_CATEGORY_NAME}' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END ASC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_CATEGORY_NAME} DESC' THEN ${DatabaseHelper.COLUMN_CATEGORY_NAME} END DESC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_AMOUNT_FACT} DESC' THEN ${DatabaseHelper.COLUMN_AMOUNT_FACT} END DESC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_AMOUNT_FACT}' THEN ${DatabaseHelper.COLUMN_AMOUNT_FACT} END ASC, " +
            "CASE WHEN :sortOrder = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) DESC' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) END DESC, " +
            "CASE WHEN :sortOrder = 'ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT})' THEN ABS(${DatabaseHelper.COLUMN_AMOUNT_FACT}) END ASC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DATE} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END DESC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DATE}' THEN ABS(${DatabaseHelper.COLUMN_DATE}) END ASC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DATE_EDIT} DESC' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END DESC, " +
            "CASE WHEN :sortOrder = '${DatabaseHelper.COLUMN_DATE_EDIT}' THEN ABS(${DatabaseHelper.COLUMN_DATE_EDIT}) END ASC"
    )
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String, sortOrder: String): Flow<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd and ${DatabaseHelper.COLUMN_REGULAR_CREATE_DATE} = :regularCreateDate LIMIT 1")
    fun getTransactionsByDateAndRegularCreateDate(timeStart: String, timeEnd: String, regularCreateDate: Long): Transaction?

    @Query("UPDATE ${DatabaseHelper.TABLE_TRANSACTION} SET ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :newName where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name")
    fun updateCategory(name: String, newName: String)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getByID(id: Long): Flow<Transaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}")
    fun getCursor(): Cursor?

}