package de.kontranik.freebudget.service

import de.kontranik.freebudget.model.RegularTransaction.month
import de.kontranik.freebudget.model.RegularTransaction.day
import de.kontranik.freebudget.model.RegularTransaction.description
import de.kontranik.freebudget.model.RegularTransaction.category
import de.kontranik.freebudget.model.RegularTransaction.amount
import de.kontranik.freebudget.model.RegularTransaction.date_start
import de.kontranik.freebudget.model.RegularTransaction.date_end
import de.kontranik.freebudget.model.RegularTransaction.date_create
import de.kontranik.freebudget.model.Transaction.regular_id
import de.kontranik.freebudget.model.Transaction.description
import de.kontranik.freebudget.model.Transaction.category
import de.kontranik.freebudget.model.Transaction.date
import de.kontranik.freebudget.model.Transaction.amount_planned
import de.kontranik.freebudget.model.Transaction.amount_fact
import de.kontranik.freebudget.model.Transaction.date_create
import de.kontranik.freebudget.model.RegularTransaction.id
import kotlin.Throws
import android.os.Environment
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.DatabaseAdapter
import android.view.View.OnTouchListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import de.kontranik.freebudget.service.OnSwipeTouchListener.GestureListener
import android.app.Activity
import android.content.Context
import de.kontranik.freebudget.model.Transaction
import java.util.*

object PlanRegular {
    fun setRegularToPlanned(context: Context?, year: Int, month: Int) {
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
                        0
                    )
                    dbAdapter.insert(transaction)
                }
            }
        }
    }
}