package de.kontranik.freebudget

import android.content.Context
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val regularTransactionRepository: RegularTransactionRepository
    val transactionRepository: TransactionRepository
    val categoryRepository: CategoryRepository
}


class AppDataContainer(private val context: Context) : AppContainer {

    override val regularTransactionRepository: RegularTransactionRepository by lazy {
        RegularTransactionRepository(
            FreeBudgetRoomDatabase.getDatabase(context).regularTransactionDao(),
            FreeBudgetRoomDatabase.getDatabase(context).categoryDao()
        )
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(
            FreeBudgetRoomDatabase.getDatabase(context).transactionDao(),
            FreeBudgetRoomDatabase.getDatabase(context).categoryDao(),
            context
        )
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(
            FreeBudgetRoomDatabase.getDatabase(context).categoryDao(),
            FreeBudgetRoomDatabase.getDatabase(context).regularTransactionDao(),
            FreeBudgetRoomDatabase.getDatabase(context).transactionDao(),
        )
    }
}
