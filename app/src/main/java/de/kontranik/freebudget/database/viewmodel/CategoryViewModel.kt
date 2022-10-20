package de.kontranik.freebudget.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.kontranik.freebudget.database.repository.CategoryRepository
import de.kontranik.freebudget.model.Category

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: CategoryRepository = CategoryRepository(application)
    val mAllCategorys: LiveData<List<Category>> = mRepository.getAll()

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    private val name = MutableLiveData<String>()
    val categoryByName: LiveData<Category?> = Transformations.switchMap(
        name,
        ::getLiveDataCategoryByName
    )
    private fun getLiveDataCategoryByName(name: String) = mRepository.getByName(name)
    fun loadCategoryByName(name: String) = apply { this.name.value = name }

    fun insert(category: Category) {
        mRepository.insert(category)
    }

    fun update(category: Category) {
        mRepository.update(category)
    }
}