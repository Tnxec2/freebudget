package de.kontranik.freebudget.ui.components.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.model.Category

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

    fun clearItem() {
        updateUiState(CategoryDetails())
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
