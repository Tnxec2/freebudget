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
import android.view.View
import java.lang.Exception

open class OnSwipeTouchListener(ctx: Context?) : OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > Companion.SWIPE_THRESHOLD && Math.abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > Companion.SWIPE_THRESHOLD && Math.abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                    super.onFling(e1, e2, velocityX, velocityY)
                    /*
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                       onSwipeTop();
                    }
                    result = true;
                    */result = false
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        companion object {
            private const val SWIPE_THRESHOLD = 100
            private const val SWIPE_VELOCITY_THRESHOLD = 100
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {} /*    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
*/

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }
}