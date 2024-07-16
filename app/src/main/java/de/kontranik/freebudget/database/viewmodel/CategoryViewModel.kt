package de.kontranik.freebudget.database.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.model.Category

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: CategoryRepository,
) : ViewModel() {

    val mAllCategorys: LiveData<List<Category>> = mRepository.getAll()

    val distinctCategoryNames = mAllCategorys.map { list ->
        list.map { category -> category.name }.distinct().sorted()
    }

}
