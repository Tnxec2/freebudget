package de.kontranik.freebudget.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction


class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: TransactionRepository = TransactionRepository(application)

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    private val id = MutableLiveData<Long>()
    val transactionById: LiveData<Transaction?> = Transformations.switchMap(
        id,
        ::getLiveDataById
    )
    private fun getLiveDataById(id: Long) = mRepository.getTransactionByID(id)
    fun loadById(id: Long) = apply { this.id.value = id }

    private val monthYearShowPlanned = MutableLiveData<Triple<Int, Int, Boolean>>()
    val dataByYearAndMonth: LiveData<List<Transaction>> = Transformations.switchMap(
        monthYearShowPlanned,
        ::getTransactionsByYearAndMonth
    )
    private fun getTransactionsByYearAndMonth(tripple: Triple<Int, Int, Boolean>) =
        mRepository.getTransactions(tripple.first, tripple.second, tripple.third)
    fun loadTransactions(e_year: Int,
                       e_month: Int,
                       showOnlyPlanned: Boolean) = apply { this.monthYearShowPlanned.value =
        Triple(e_year, e_month, showOnlyPlanned) }

    fun insert(transaction: Transaction) {
        mRepository.insert(transaction)
    }

    fun update(transaction: Transaction) {
        mRepository.update(transaction)
    }

    fun exportToFile(fileName: String): String {
        return mRepository.exportToCSV(fileName)
    }
}