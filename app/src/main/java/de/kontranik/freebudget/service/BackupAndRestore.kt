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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by denny on 16/05/2016.
 * Source: http://stackoverflow.com/questions/18322401/is-it-posible-backup-and-restore-a-database-file-in-android-non-root-devices
 */
object BackupAndRestore {
    @Throws(IOException::class)
    fun importDB(context: Context): Boolean {
        val sd = Environment.getExternalStorageDirectory()
        if (sd.canWrite()) {
            val backupDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
            val backupDBPath = String.format("%s.bak", DatabaseHelper.DATABASE_NAME)
            val currentDB = File(sd, backupDBPath)
            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            return true
        }
        return false
    }

    @Throws(IOException::class)
    fun exportDB(context: Context): Boolean {
        val sd = Environment.getExternalStorageDirectory()
        val data = Environment.getDataDirectory()
        if (sd.canWrite()) {
            val backupDBPath = String.format("%s.bak", DatabaseHelper.DATABASE_NAME)
            val currentDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
            val backupDB = File(sd, backupDBPath)
            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            return true
        }
        return false
    }
}