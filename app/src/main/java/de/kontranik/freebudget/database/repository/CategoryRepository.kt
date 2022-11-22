package de.kontranik.freebudget.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase

import de.kontranik.freebudget.database.dao.CategoryDao
import de.kontranik.freebudget.database.dao.RegularTransactionDao
import de.kontranik.freebudget.database.dao.TransactionDao
import de.kontranik.freebudget.model.Category


internal class CategoryRepository(context: Context) {
    private val mCategoryDao: CategoryDao
    private val mRegularTransactionDao: RegularTransactionDao
    private val mTransactionDao: TransactionDao

    init {
        val db: FreeBudgetRoomDatabase = FreeBudgetRoomDatabase.getDatabase(context)
        mCategoryDao = db.categoryDao()
        mRegularTransactionDao = db.regularTransactionDao()
        mTransactionDao = db.transactionDao()
    }

    fun insert(category: Category) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            category.name = category.name.trim()
            if (category.name.isNotEmpty()) {
                val categoryInDB = mCategoryDao.getByName(category.name)
                if ( categoryInDB == null)
                    mCategoryDao.insert(category)
            }
        }
    }

    fun getAll(): LiveData<List<Category>> {
        return mCategoryDao.getAll
    }

    fun getById(id: Long): LiveData<Category> {
        return mCategoryDao.getLiveById(id)
    }

    fun getByName(name: String): LiveData<Category> {
        return mCategoryDao.getLiveByName(name)
    }

    fun delete(id: Long) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            val categoryInDB = mCategoryDao.getById(id)
            if (categoryInDB != null) {
                mCategoryDao.delete(id)
                mTransactionDao.updateCategory(categoryInDB.name, "")
                mRegularTransactionDao.updateCategory(categoryInDB.name, "")
            }
        }
    }

    fun update(category: Category) {
        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            category.name = category.name.trim()
            if (category.name.isNotEmpty()) {
                if ( category.id != null) {
                    val categoryInDB = mCategoryDao.getById(category.id!!)
                    if (categoryInDB != null) {
                        mCategoryDao.update(category)
                        mTransactionDao.updateCategory(categoryInDB.name, category.name)
                        mRegularTransactionDao.updateCategory(categoryInDB.name, category.name)
                    }
                }
            }
        }
    }
}