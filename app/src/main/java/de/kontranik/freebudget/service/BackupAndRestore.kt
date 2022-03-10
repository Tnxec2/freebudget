package de.kontranik.freebudget.service

import android.content.Context
import android.os.Environment
import de.kontranik.freebudget.database.DatabaseHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by denny on 16/05/2016.
 * Source: http://stackoverflow.com/questions/18322401/is-it-posible-backup-and-restore-a-database-file-in-android-non-root-devices
 */
object BackupAndRestore {
    @JvmStatic
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

    @JvmStatic
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