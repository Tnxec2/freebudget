package de.kontranik.freebudget.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.kontranik.freebudget.FreebudgetApplication
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.ui.components.alltransactions.AllTransactionsScreenViewModel
import de.kontranik.freebudget.ui.components.alltransactions.TransactionItemViewModel
import de.kontranik.freebudget.ui.components.category.CategoryItemViewModel
import de.kontranik.freebudget.ui.components.regular.RegularTransactionItemViewModel
import de.kontranik.freebudget.ui.components.settings.SettingsViewModel
import de.kontranik.freebudget.ui.components.tools.ToolsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            RegularTransactionViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.regularTransactionRepository
            )
        }

        initializer {
            RegularTransactionItemViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.regularTransactionRepository
            )
        }

        initializer {
            AllTransactionsScreenViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            TransactionViewModel(
                inventoryApplication().container.transactionRepository,
                inventoryApplication().container.regularTransactionRepository,
            )
        }

        initializer {
            TransactionItemViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.transactionRepository
            )
        }

        initializer {
            CategoryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.categoryRepository,
            )
        }

        initializer {
            CategoryItemViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.categoryRepository,
            )
        }

        initializer {
            SettingsViewModel(
                inventoryApplication().applicationContext,
            )
        }

        initializer {
            ToolsViewModel(
                inventoryApplication().container.transactionRepository,
                inventoryApplication().container.regularTransactionRepository,
            )
        }
    }
}

/**
 * Extension function to queries for [FreeBudgetApplication] object and returns an instance of
 * [FreeBudgetApplication].
 */
fun CreationExtras.inventoryApplication(): FreebudgetApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FreebudgetApplication)
