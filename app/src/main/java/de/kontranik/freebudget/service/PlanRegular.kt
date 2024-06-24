package de.kontranik.freebudget.service

import android.content.Context
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction
import java.util.*

object PlanRegular {
    @JvmStatic
    fun setRegularToPlanned(context: Context, year: Int, month: Int) {
        val transactionRepository = TransactionRepository(context)
        val regularTransactionRepository = RegularTransactionRepository(context)

        /*
         *     aktuellen Monat und allgemeine (Monat == 0)
         */

        FreeBudgetRoomDatabase.databaseWriteExecutor.execute {
            val regularTransactionList = regularTransactionRepository.getTransactionsByMonthNoLiveData(month).toMutableList()
            regularTransactionList.addAll(regularTransactionRepository.getTransactionsByMonthNoLiveData(0))
            for (rt in regularTransactionList) {
                val transaction = transactionRepository.getTransactionByRegularCreateDate(year, month, rt.dateCreate)
                if ( transaction == null) {
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
                    if ( rt.isDateInScope(cal.timeInMillis) ) {
                        val newTransaction = Transaction(
                            id = null,
                            regularCreateTime = rt.dateCreate,
                            description =  rt.description,
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


}