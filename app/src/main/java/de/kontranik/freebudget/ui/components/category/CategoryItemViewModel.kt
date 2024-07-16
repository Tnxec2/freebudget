package de.kontranik.freebudget.ui.components.category

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.TransactionType
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

class CategoryItemViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: CategoryRepository
) : ViewModel() {

    var transactionItemUiState by mutableStateOf(CategoryDetailsUiState())
        private set


    fun save(category: Category) {
        if (category.name.isEmpty()) return

        if (category.id != null)
            mRepository.update(category)
        else {
            mRepository.insert(category)
        }
    }

    fun delete(category: Category) {
        category.id?.let {
            mRepository.delete(it)
        }
    }

    fun updateUiState(categoryDetails: CategoryDetails) {
        transactionItemUiState =
            CategoryDetailsUiState(categoryDetails = categoryDetails)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class CategoryDetailsUiState(val categoryDetails: CategoryDetails = CategoryDetails())

data class CategoryDetails(val id: Long? = null, val name: String = "")

fun CategoryDetails.toCategrory(
    ): Category {
    return Category(
        id,
        name
    )
}

fun Category.toCategoryDetails(): CategoryDetails {
    return CategoryDetails(
        id,
        name
    )
}
