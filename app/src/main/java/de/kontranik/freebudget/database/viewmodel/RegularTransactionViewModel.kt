package de.kontranik.freebudget.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.model.RegularTransaction

class RegularTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: RegularTransactionRepository = RegularTransactionRepository(application)



    fun delete(id: Long) {
        mRepository.delete(id)
    }

    private val id = MutableLiveData<Long?>()
    val regularTransactionById: LiveData<RegularTransaction> = id.switchMap{
        getLiveDataRegularTransactionById(it)}
    private fun getLiveDataRegularTransactionById(id: Long?) = if (id != null) mRepository.getById(id) else null
    fun loadRegularTransactionsById(id: Long) = apply { this.id.value = id }
    fun clearRegularTransactionsById() = apply { this.id.value = null }

    private val month = MutableLiveData(0)
    val regularTransactionByMonth: LiveData<List<RegularTransaction>> = month.switchMap{
        getLiveDataRegularTransactionByMonth(it) }
    private fun getLiveDataRegularTransactionByMonth(month: Int) = mRepository.getTransactionsByMonth(month)
    fun loadRegularTransactionsByMonth(month: Int) = apply { this.month.value = month }
    fun getMonth(): MutableLiveData<Int> {
        return month
    }
    fun insert(regularTransaction: RegularTransaction) {
        mRepository.insertRegularTransaction(regularTransaction)
    }

    fun update(regularTransaction: RegularTransaction) {
        mRepository.update(regularTransaction)
    }

    fun prevMonth() {
        month.value = if (month.value == 0) {
            12
        } else {
            month.value?.minus(1)
        }
    }

    fun nextMonth() {
        month.value = if (month.value == 12) {
            0
        } else {
            month.value?.plus(1)
        }
    }
}