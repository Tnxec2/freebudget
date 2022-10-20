package de.kontranik.freebudget.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.RegularTransaction


@Dao
interface RegularTransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRegularTransaction(regularTransaction: RegularTransaction)

    @Update
    fun update(regularTransaction: RegularTransaction)

    @Query("DELETE FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR}")
    fun getAllLiveData(): LiveData<List<RegularTransaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR}  ORDER BY :sortOrder")
    fun getAllLiveData(sortOrder: String): LiveData<List<RegularTransaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR}")
    fun getAll(): List<RegularTransaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} ORDER BY :sortOrder")
    fun getAll(sortOrder: String): List<RegularTransaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getById(id: Long): LiveData<RegularTransaction>

    @Query("UPDATE ${DatabaseHelper.TABLE_REGULAR} SET ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :newName where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name")
    fun updateCategory(name: String, newName: String)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_MONTH} = :month order by ${DatabaseHelper.COLUMN_DAY}")
    fun getByMonth(month: Int): LiveData<List<RegularTransaction>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR} where ${DatabaseHelper.COLUMN_MONTH} = :month order by ${DatabaseHelper.COLUMN_DAY}")
    fun getByMonthNoLiveData(month: Int): List<RegularTransaction>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_REGULAR}")
    fun getCursor(): Cursor?
}