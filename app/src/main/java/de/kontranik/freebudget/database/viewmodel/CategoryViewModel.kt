package de.kontranik.freebudget.database.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.model.Category

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val mRepository: CategoryRepository,
) : ViewModel() {

    val mAllCategorys: LiveData<List<Category>> = mRepository.getAll()

    fun delete(category: Category) {
        category.id?.let {
            mRepository.delete(it)
        }
    }

    private val name = MutableLiveData<String>()
    val categoryByName: LiveData<Category> = name.switchMap {
        getLiveDataCategoryByName(it) }
    private fun getLiveDataCategoryByName(name: String) = mRepository.getByName(name)
    fun loadCategoryByName(name: String) = apply { this.name.value = name }

    fun insert(category: Category) {
        mRepository.insert(category)
    }

    fun update(category: Category) {
        mRepository.update(category)
    }

    fun onSave(category: Category) {
        if (category.name.isEmpty()) return

        if (category.id != null)
            update(category)
        else
            insert(category)
    }
}
