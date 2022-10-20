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

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd and ${DatabaseHelper.COLUMN_AMOUNT_FACT} = 0")
    fun getPlannedTransactionsByDate(timeStart: String, timeEnd: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd and ${DatabaseHelper.COLUMN_AMOUNT_FACT} = 0  ORDER BY :sortOrder")
    fun getPlannedTransactionsByDate(timeStart: String, timeEnd: String, sortOrder: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd")
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd ORDER BY :sortOrder")
    fun getAllTransactionsByDate(timeStart: String, timeEnd: String, sortOrder: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_DATE} >= :timeStart and ${DatabaseHelper.COLUMN_DATE} < :timeEnd and ${DatabaseHelper.COLUMN_REGULAR_CREATE_DATE} = :regularCreateDate LIMIT 1")
    fun getTransactionsByDateAndRegularCreateDate(timeStart: String, timeEnd: String, regularCreateDate: Long): Transaction?

    @Query("UPDATE ${DatabaseHelper.TABLE_TRANSACTION} SET ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :newName where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name")
    fun updateCategory(name: String, newName: String)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getByID(id: Long): LiveData<Transaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}")
    fun getCursor(): Cursor?

}