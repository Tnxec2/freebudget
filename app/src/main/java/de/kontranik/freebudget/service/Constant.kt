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

object Constant {
    const val TRANS_STAT = "TRANS_STAT"
    const val TRANS_STAT_PLUS = "plus"
    const val TRANS_STAT_MINUS = "minus"
    const val TRANS_TYP = "TRANS_TYP"
    const val TRANS_TYP_PLANNED = "planned"
    const val TRANS_TYP_FACT = "fact"
    const val TRANS_ID = "id"
}