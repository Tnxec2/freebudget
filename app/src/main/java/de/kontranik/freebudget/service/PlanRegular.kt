package de.kontranik.freebudget.service

import android.content.Context
import de.kontranik.freebudget.database.DatabaseAdapter
import de.kontranik.freebudget.model.Transaction
import java.util.*

object PlanRegular {
    @JvmStatic
    fun setRegularToPlanned(context: Context, year: Int, month: Int) {
        val dbAdapter = DatabaseAdapter(context)
        dbAdapter.open()

        /*
         *     aktuellen Monat und allgemeine (Monat == 0)
         */
        val regularTransactionList = dbAdapter.getRegular(month)
        regularTransactionList.addAll(dbAdapter.getRegular(0))
        for (rt in regularTransactionList) {
            if (!dbAdapter.checkTransactions(year, month, rt.id!!)) {
                /*
                 * prüfen, wenn Tag grösser als aktueller monat zulässt,
                 * dann auf letzen tag des monats setzen
                 */
                var cal: Calendar = GregorianCalendar(year, month - 1, 1, 0, 0, 1)
                val i = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (rt.day > i) rt.day = i
                cal = GregorianCalendar(year, month - 1, rt.day, 0, 0, 1)

                /*
                 * inserten nur wenn Datum im Rahmen von START-END oder START-END nicht eingegeben sind
                 */if (rt.date_start > 0 && cal.getTimeInMillis() >= rt.date_start
                    ||
                    rt.date_end > 0 && cal.getTimeInMillis() <= rt.date_end
                    ||
                    rt.date_start == 0L && rt.date_end == 0L
                ) {
                    val transaction = Transaction(
                        0,
                        rt.id!!,
                        rt.description!!,
                        rt.category!!,
                        cal.getTimeInMillis(),
                        rt.amount,
                        0.0
                    )
                    dbAdapter.insert(transaction)
                }
            }
        }
    }
}