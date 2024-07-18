package de.kontranik.freebudget.ui.components.alltransactions

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction

class AllTransactionsScreenViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val categoryName: String? =
        savedStateHandle[AllTransactionsScreenDestination.CATEGORY_NAME_ARG]

    fun isValid(transaction: Transaction, showOnlyPlanned: Boolean): Boolean {
        return (
                isCategoryValid(categoryName, transaction)
                    &&
                (!showOnlyPlanned || transaction.amountFact == 0.0)
            )
    }

    private fun isCategoryValid(categoryName: String?, transaction: Transaction): Boolean {
        return (categoryName == null || transaction.category.lowercase().trim() == categoryName.lowercase().trim())
    }

    fun getTitle(showOnlyPlanned: Boolean, context: Context): String {
        return if (showOnlyPlanned)
            if (categoryName == null)
                context.getString(R.string.title_all_transactions_planned)
            else
                context.getString(
                    R.string.title_all_transactions_category_planned,
                    categoryName
                )
        else
            if (categoryName == null)
                context.getString(R.string.title_all_transactions)
            else
                context.getString(R.string.title_all_transactions_category, categoryName)
    }
}


