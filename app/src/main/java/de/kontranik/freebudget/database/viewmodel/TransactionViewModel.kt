package de.kontranik.freebudget.database.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.helpers.DateUtils
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.abs


class TransactionViewModel(
    private val context: Context,
    private val transactionRepository: TransactionRepository,
    private val regularTransactionRepository: RegularTransactionRepository
) : ViewModel() {

    fun delete(id: Long) {
        transactionRepository.delete(id)
        //getTransactions()
    }

    lateinit var query: MutableLiveData<TransactionQuery>

    init {
        println("init query")
        query = MutableLiveData(TransactionQuery())
    }

    val transactionsUiState = query.switchMap { query ->
        transactionRepository.getTransactions(query.year, query.month)
            .map { list -> TransactionsUiState(list, getCategroySummary(list)) }.asLiveData()
    }

    private fun getCategroySummary(transactionList: List<Transaction>): MutableMap<String, Category> {
        var maxCategoryWeight = 0.0
        val notDefined = context.getString(R.string.category_not_defined)
        val categoryList = mutableMapOf<String, Category>()

        transactionList.forEach { transaction ->
            var categoryName = transaction.category.trim()
            if (categoryName.isEmpty()) categoryName = notDefined
            if (transaction.amountFact < 0) {
                if (categoryList.containsKey(categoryName)) {
                    categoryList[categoryName]?.let {
                        it.weight += abs(transaction.amountFact)
                    }
                } else {
                    categoryList[categoryName] = Category(0, categoryName, abs(transaction.amountFact))
                }
                categoryList[categoryName]?.let {
                    if (it.weight > maxCategoryWeight) maxCategoryWeight = it.weight
                }
            }
        }
        return categoryList
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
    val year: Int = DateUtils.getYear(),
    val month: Int = DateUtils.getMonth(),
)


data class TransactionsUiState(
    val itemList: List<Transaction> = listOf(),
    val categorySummary: MutableMap<String, Category> = mutableMapOf()
)
