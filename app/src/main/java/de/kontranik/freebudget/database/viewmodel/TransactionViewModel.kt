package de.kontranik.freebudget.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction


class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: TransactionRepository = TransactionRepository(application)

    fun delete(id: Long) {
        mRepository.delete(id)
    }

    private val id = MutableLiveData<Long>()
    val transactionById: LiveData<Transaction> = id.switchMap{
        getLiveDataById(it)}
    private fun getLiveDataById(id: Long) = mRepository.getTransactionByID(id)
    fun loadById(id: Long) = apply { this.id.value = id }

    private val monthYearShowPlanned = MutableLiveData<TransactionQuery>()
    val dataByYearAndMonth: LiveData<List<Transaction>> = monthYearShowPlanned.switchMap{
        getTransactionsByYearAndMonth(it)}
    private fun getTransactionsByYearAndMonth(query: TransactionQuery) =
        mRepository.getTransactions(query.year, query.month, query.category, query.showOnlyPlanned)

    fun loadTransactions(e_year: Int,
                         e_month: Int,
                         category: String?,
                         showOnlyPlanned: Boolean) = apply { this.monthYearShowPlanned.value =
        TransactionQuery(e_year, e_month, category, showOnlyPlanned) }

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

data class TransactionQuery(val year: Int, val month: Int, val category: String? = null, val showOnlyPlanned: Boolean)