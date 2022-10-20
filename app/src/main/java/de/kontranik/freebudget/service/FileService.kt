package de.kontranik.freebudget.service


import kotlin.Throws
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.R
import android.content.Context
import android.net.Uri
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.database.repository.RegularTransactionRepository
import de.kontranik.freebudget.database.repository.TransactionRepository
import de.kontranik.freebudget.model.Transaction
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object FileService {
    @JvmStatic
    @Throws(Exception::class)
    fun importFileRegular(uri: Uri, context: Context): Boolean {

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

        val mRepository = RegularTransactionRepository(context)
        for (rt in regularTransactionList) {
            mRepository.insertRegularTransaction(rt)
        }

        return true
    }

    @JvmStatic
    @Throws(IOException::class)
    fun exportFileRegular(baseFileName: String, context: Context): String {
        val mRepository = RegularTransactionRepository(context)
        return mRepository.exportToCSV(baseFileName)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun importFileTransaction(uri: Uri, context: Context): Boolean {
        val contentResolver = context.contentResolver

        val transactionList: MutableList<Transaction> = ArrayList()

        val dfShort = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val dfLong= SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val dfMillis = SimpleDateFormat(Config.DATE_MILLIS, Locale.US)

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
                            R.string.wrongLineFormat,
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
        val mRepository = TransactionRepository(context)

        for (transaction in transactionList) {
            mRepository.insert(transaction)
        }

        return true
    }

    private fun getDouble(token: String): Double {
        if (token.isBlank() || token.isEmpty()) return 0.0
        return token.replace(',', '.').toDouble()
    }
}