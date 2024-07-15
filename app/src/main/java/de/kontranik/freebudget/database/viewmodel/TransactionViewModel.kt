package de.kontranik.freebudget.database.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.GregorianCalendar


class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val regularTransactionRepository: RegularTransactionRepository
) : ViewModel() {

    fun delete(id: Long) {
        transactionRepository.delete(id)
        //getTransactions()
    }

    var query = MutableLiveData(TransactionQuery())

    val transactionsUiState = query.switchMap { query ->
        transactionRepository.getTransactions(query.year, query.month)
            .map { list -> TransactionsUiState(list) }.asLiveData()
    }

    fun insert(transaction: Transaction) {
        transactionRepository.insert(transaction)
    }

    fun update(transaction: Transaction) {
        transactionRepository.update(transaction)
    }

    fun exportToFile(fileName: String): String {
        return transactionRepository.exportToCSV(fileName)
    }

    fun prevMonth() {
        query.value?.let {
            var month = it.month
            var year = it.year
            if (month == 1) {
                if (year == 2000) return
                month = 12
                year--
            } else {
                month -= 1
            }

            query.value = TransactionQuery(
                year,
                month
            )
        }

    }

    fun nextMonth() {
        query.value?.let {
            var month = it.month
            var year = it.year
            if (month == 12) {
                if (year == 3000) return
                month = 1
                year++
            } else {
                month += 1
            }
            query.value = TransactionQuery(
                year,
                month
            )
        }
    }

    fun planRegular() {
        val month = query.value!!.month
        val year = query.value!!.year

        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            val regularTransactionList =
                regularTransactionRepository.getTransactionsByMonthNoLiveData(month).toMutableList()
            regularTransactionList.addAll(regularTransactionRepository.getTransactionsByMonthNoLiveData(0))
            for (rt in regularTransactionList) {
                val transaction =
                    transactionRepository.getTransactionByRegularCreateDate(year, month, rt.dateCreate)
                if (transaction == null) {
                    /*
                     * prüfen, wenn Tag grösser als aktueller monat zulässt,
                     * dann auf letzen tag des monats setzen
                     */
                    var cal: Calendar = GregorianCalendar(year, month - 1, 1, 0, 0, 1)
                    val actualMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (rt.day > actualMaximum) rt.day = actualMaximum
                    cal = GregorianCalendar(year, month - 1, rt.day, 0, 0, 1)

                    /*
                     * inserten nur wenn Datum im Rahmen von START-END oder START-END nicht eingegeben sind
                     */
                    if (rt.isDateInScope(cal.timeInMillis)) {
                        val newTransaction = Transaction(
                            id = null,
                            regularCreateTime = rt.dateCreate,
                            description = rt.description,
                            category = rt.category,
                            date = cal.timeInMillis,
                            amountPlanned = rt.amount,
                            amountFact = 0.0,
                            note = rt.note
                        )
                        transactionRepository.insert(newTransaction)
                    }
                }
            }
        }

    }

    fun exportToCSV(baseFileName: String): String {
        return transactionRepository.exportToCSV(baseFileName)
    }

    fun insertAll(transactionList: MutableList<Transaction>) {
        for (transaction in transactionList) {
            transactionRepository.insert(transaction)
        }
    }
}

data class TransactionQuery(
    val year: Int = Calendar.getInstance()[Calendar.YEAR],
    val month: Int = Calendar.getInstance()[Calendar.MONTH] + 1,
)


data class TransactionsUiState(val itemList: List<Transaction> = listOf())
