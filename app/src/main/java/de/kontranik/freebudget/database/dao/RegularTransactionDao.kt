package de.kontranik.freebudget.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.RegularTransaction
import kotlinx.coroutines.flow.Flow


@Dao
interface RegularTransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRegularTransaction(regularTransaction: RegularTransaction)

    @Update
    fun update(regularTransaction: RegularTransaction)

    @Query("DELETE FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getById(id: Long): Flow<RegularTransaction>

    @Query("UPDATE ${DatabaseHelper.TABLE_REGULAR} " +
            "SET ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :newName " +
            "where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name")
    fun updateCategory(name: String, newName: String)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} " +
            " where ${DatabaseHelper.COLUMN_MONTH} = :month " +
            " or (:month > 0 and ${DatabaseHelper.COLUMN_MONTH} = 0) " +
            " order by ${DatabaseHelper.COLUMN_DAY}")
    fun getByMonth(month: Int): Flow<List<RegularTransaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} " +
            "where ${DatabaseHelper.COLUMN_MONTH} = :month " +
            "order by ${DatabaseHelper.COLUMN_DAY}")
    fun getByMonthNoLiveData(month: Int): List<RegularTransaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR}")
    fun getCursor(): Cursor?
}