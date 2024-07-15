package de.kontranik.freebudget.ui.components.tools

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.DatabaseHelper
import de.kontranik.freebudget.database.FreeBudgetRoomDatabase
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.model.Transaction
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ToolsViewModel(
    private val transactionRepository: TransactionRepository,
    private val regularTransactionRepository: RegularTransactionRepository
) : ViewModel() {


    fun exportRegular(): String {
        val filename = "export_freebudget_regular_transaction"
        return regularTransactionRepository.exportToCSV(filename)
    }

    fun exportNormal(): String {
        val filename = "export_freebudget_transaction"
        return transactionRepository.exportToCSV(filename)
    }

    @Throws(Exception::class)
    fun importFileRegularTransactions(uri: Uri, context: Context): Boolean {

        val contentResolver = context.contentResolver

        val regularTransactionList: MutableList<RegularTransaction> = ArrayList()

        val simpleDateFormatShort = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val simpleDateFormatLong = SimpleDateFormat(Config.DATE_LONG, Locale.US)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                // BOM marker will only appear on the very beginning
                br.mark(4)
                if ('\ufeff'.code != br.read()) br.reset() // not the BOM marker

                var line: String? = br.readLine()

                while (line != null) {
                    val str = line.split(Config.CSV_DELIMITER).toTypedArray()
                    val transaction = RegularTransaction()
                    for (i in str.indices) {
                        val token = str[i].trim()
                        when (i) {
                            0 -> transaction.month = token.toInt()
                            1 -> transaction.day = token.toInt()
                            2 -> transaction.description = token
                            3 -> transaction.category = token
                            4 -> transaction.amount = getDouble(token)
                            5 -> transaction.dateStart = if (token.isBlank()) System.currentTimeMillis() else simpleDateFormatShort.parse(token)!!.time
                            6 -> transaction.dateEnd = if (token.isBlank()) null else simpleDateFormatShort.parse(token)!!.time
                            7 -> transaction.dateCreate = if (token.isBlank()) System.nanoTime() else simpleDateFormatLong.parse(token)!!.time
                            8 -> transaction.note = token
                        }
                    }
                    if (transaction.month < 0 || transaction.month > 12) throw Exception(
                        context.resources.getString(
                            R.string.wrongMonthInTheLine,
                            line
                        )
                    )
                    if (transaction.day < 1 || transaction.day > 31) throw Exception(
                        context.resources.getString(
                            R.string.wrongDayInTheLine,
                            line
                        )
                    )
                    if (transaction.description.isBlank()) throw Exception(
                        context.resources.getString(
                            R.string.wrongLineFormat,
                            line
                        )
                    )
                    regularTransactionList.add(transaction)
                    line = br.readLine()
                }
            }
        }

        regularTransactionRepository.insertAll(regularTransactionList)


        return true
    }

    @Throws(Exception::class)
    fun importFileNormalTransaction(uri: Uri, context: Context): Boolean {
        val contentResolver = context.contentResolver

        val transactionList: MutableList<Transaction> = ArrayList()

        val dfShort = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val dfLong= SimpleDateFormat(Config.DATE_LONG, Locale.US)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                // BOM marker will only appear on the very beginning
                br.mark(4)
                if ('\ufeff'.code != br.read()) br.reset() // not the BOM marker

                var line: String? = br.readLine()
                while (line != null) {
                    val str = line.split(Config.CSV_DELIMITER).toTypedArray()
                    val transaction = Transaction()
                    for (i in str.indices) {
                        val token = str[i].trim()
                        when (i) {
                            // 0 -> id, unnecessary
                            1 -> transaction.description = token
                            2 -> transaction.category = token
                            3 -> transaction.date =
                                if (token.isBlank()) 0L else dfShort.parse(token)!!.time
                            4 -> transaction.amountPlanned = getDouble(token)
                            5 -> transaction.amountFact = getDouble(token)
                            6 -> transaction.dateCreate =
                                if (token.isBlank()) System.currentTimeMillis() else dfLong.parse(
                                    token
                                )!!.time
                            7 -> transaction.dateEdit =
                                if (token.isBlank()) System.currentTimeMillis() else dfLong.parse(
                                    token
                                )!!.time
                            8 -> transaction.note = token
                            9 -> transaction.regularCreateTime =
                                if (token.isBlank()) null else token.toLong()
                        }
                    }
                    if (transaction.date == 0L) throw Exception(
                        context.resources.getString(
                            de.kontranik.freebudget.R.string.wrongLineFormat,
                            line
                        )
                    )
                    if (transaction.description.isBlank()) throw Exception(
                        context.resources.getString(
                            R.string.wrongLineFormat,
                            line
                        )
                    )
                    transactionList.add(transaction)
                    line = br.readLine()
                }
            }
        }

        transactionRepository.insertAll(transactionList)

        return true
    }

    fun backupDB(context: Context): String {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (directory.canWrite()) {
            val dfLong: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
            val backupDBPath = String.format("%s_%s.backup", DatabaseHelper.DATABASE_NAME, dfLong.format(Date()))
            val currentDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
            val backupDB = File(directory, backupDBPath)
            transactionRepository.checkPoint() // to ensure all of the pending transactions are applied
            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            return backupDB.path
        }
        throw IOException("Can not write to ${directory.path}")
    }

    fun importDB(uri: Uri, context: Context): Boolean {
        val contentResolver = context.contentResolver
        val oldDB = FreeBudgetRoomDatabase.getDatabase(context).openHelper.writableDatabase.path

        val src = contentResolver.openInputStream(uri)

        if (src != null) {
            try {
                copyFile(src as FileInputStream, FileOutputStream(oldDB))
                return true
            } catch (e: IOException) {
                Log.e("RESTORE", "ex for is of restore: $e")
                e.printStackTrace()
            }
        } else {
            Log.d("RESTORE", "Restore - file does not exists")
        }
        return false
    }
}

@Throws(IOException::class)
fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {

    try {
        val buffer = ByteArray(1024)
        var length: Int
        while ((fromFile.read(buffer).also { length = it }) > 0) {
            toFile.write(buffer, 0, length)
        }
        toFile.flush()
    } finally {
        fromFile.close()
        toFile.close()
    }
}

private fun getDouble(token: String): Double {
    if (token.isBlank() || token.isEmpty()) return 0.0
    return token.replace(',', '.').toDouble()
}