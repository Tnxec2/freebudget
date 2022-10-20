package de.kontranik.freebudget.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.model.RegularTransaction

class RegularTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: RegularTransactionRepository = RegularTransactionRepository(application)

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    private val id = MutableLiveData<Long?>()
    val regularTransactionById: LiveData<RegularTransaction?> = Transformations.switchMap(
        id,
        ::getLiveDataRegularTransactionById
    )
    private fun getLiveDataRegularTransactionById(id: Long?) = if (id != null) mRepository.getById(id) else null
    fun loadRegularTransactionsById(id: Long) = apply { this.id.value = id }
    fun clearRegularTransactionsById() = apply { this.id.value = null }

    private val month = MutableLiveData<Int>()
    val regularTransactionByMonth: LiveData<List<RegularTransaction>> = Transformations.switchMap(
        month,
        ::getLiveDataRegularTransactionByMonth
    )
    private fun getLiveDataRegularTransactionByMonth(month: Int) = mRepository.getTransactionsByMonth(month)
    fun loadRegularTransactionsByMonth(month: Int) = apply { this.month.value = month }

    fun insert(regularTransaction: RegularTransaction) {
        mRepository.insertRegularTransaction(regularTransaction)
    }

    fun update(regularTransaction: RegularTransaction) {
        mRepository.update(regularTransaction)
    }
}