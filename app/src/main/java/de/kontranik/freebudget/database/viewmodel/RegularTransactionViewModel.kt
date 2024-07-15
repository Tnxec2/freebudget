package de.kontranik.freebudget.database.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.model.RegularTransaction
import kotlinx.coroutines.flow.map


class RegularTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val regularTransactionRepository: RegularTransactionRepository
) : ViewModel() {

    fun delete(id: Long) {
        regularTransactionRepository.delete(id)
    }

    private val month = MutableLiveData(0)

    val regularTRansactionsUiState = month.switchMap { month ->
        regularTransactionRepository.getTransactionsByMonth(month)
            .map { list -> RegularTransactionsUiState(list) }.asLiveData()
    }

    fun getMonth(): MutableLiveData<Int> {
        return month
    }

    fun insert(regularTransaction: RegularTransaction) {
        regularTransactionRepository.insertRegularTransaction(regularTransaction)
    }

    fun update(regularTransaction: RegularTransaction) {
        regularTransactionRepository.update(regularTransaction)
    }

    fun prevMonth() {
        month.value = if (month.value == 0) {
            12
        } else {
            month.value!!.minus(1)
        }
    }

    fun nextMonth() {
        month.value = if (month.value == 12) {
            0
        } else {
            month.value!!.plus(1)
        }
    }

    fun save(rt: RegularTransaction) {
        if (rt.id == null)
            insert(rt)
        else
            update(rt)
    }

    fun insertAll(regularTransactionList: MutableList<RegularTransaction>) {
        for (rt in regularTransactionList) {
            regularTransactionRepository.insertRegularTransaction(rt)
        }
    }

    fun exportToCSV(baseFileName: String): String {
        return regularTransactionRepository.exportToCSV(baseFileName)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class RegularTransactionsUiState(val itemList: List<RegularTransaction> = listOf())
