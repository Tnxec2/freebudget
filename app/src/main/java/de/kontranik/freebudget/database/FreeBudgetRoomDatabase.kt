package de.kontranik.freebudget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.kontranik.freebudget.database.dao.CategoryDao
import de.kontranik.freebudget.database.dao.RegularTransactionDao
import de.kontranik.freebudget.database.dao.TransactionDao
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.model.Transaction
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// https://developer.android.com/codelabs/android-room-with-a-view#7

@Database(entities = [Category::class, RegularTransaction::class, Transaction::class],
    version = FreeBudgetRoomDatabase.SCHEMA,
    exportSchema = false)
abstract class FreeBudgetRoomDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun regularTransactionDao(): RegularTransactionDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: FreeBudgetRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(
            NUMBER_OF_THREADS
        )

        fun getDatabase(context: Context): FreeBudgetRoomDatabase {
            if (INSTANCE == null) {
                synchronized(FreeBudgetRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            FreeBudgetRoomDatabase::class.java,
                            DATABASE_NAME
                        )
                            .setJournalMode(JournalMode.TRUNCATE)
                            .addMigrations()
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private const val DATABASE_NAME = "freebudget.db" // db name
        internal const val SCHEMA = 1 // db version

    }
}
