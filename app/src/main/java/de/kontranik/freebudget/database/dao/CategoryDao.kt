package de.kontranik.freebudget.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Query("DELETE FROM ${DatabaseHelper.TABLE_CATEGORY} where ${DatabaseHelper.COLUMN_ID} = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORY} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getLiveById(id: Long): LiveData<Category>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORY} where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name LIMIT 1")
    fun getLiveByName(name: String): LiveData<Category>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORY} where ${DatabaseHelper.COLUMN_CATEGORY_NAME} = :name LIMIT 1")
    fun getByName(name: String): Category?

    @get:Query("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORY}")
    val getAll: LiveData<List<Category>>

    @Query("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORY} where ${DatabaseHelper.COLUMN_ID} = :id LIMIT 1")
    fun getById(id: Long): Category?
}