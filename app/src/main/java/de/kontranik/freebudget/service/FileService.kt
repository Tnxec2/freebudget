package de.kontranik.freebudget.service


import kotlin.Throws
import android.os.Environment
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.DatabaseAdapter
import android.content.Context
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.model.Transaction
import java.io.*
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object FileService {
    @JvmStatic
    @Throws(Exception::class)
    fun importFileRegular(filename: String?, context: Context): Boolean {
        val regularTransactionList: MutableList<RegularTransaction> = ArrayList()
        val br = BufferedReader(InputStreamReader(FileInputStream(filename), Config.CSV_CODE_PAGE))
        var line: String

        // BOM marker will only appear on the very beginning
        br.mark(4)
        if ('\ufeff'.toInt() != br.read()) br.reset() // not the BOM marker
        while (br.readLine().also { line = it } != null) {
            val str = line.split(Config.CSV_DELIMITER).toTypedArray()
            if (str.size > 6) {
                val s_id: Long = 0
                val s_month = Integer.valueOf(str[0].trim { it <= ' ' })
                if (s_month < 0 || s_month > 12) throw Exception(
                    context.resources.getString(
                        R.string.wrongMonthInTheLine,
                        line
                    )
                )
                val s_day = Integer.valueOf(str[1].trim { it <= ' ' })
                if (s_day < 1 || s_day > 31) throw Exception(
                    context.resources.getString(
                        R.string.wrongDayInTheLine,
                        line
                    )
                )
                val s_description = str[2].trim { it <= ' ' }
                val s_category = str[3].trim { it <= ' ' }
                val s_amount = java.lang.Double.valueOf(str[4].trim { it <= ' ' }.replace(',', '.'))
                var s_date_start: Long = 0
                if (str[5].trim { it <= ' ' }.length > 0) {
                    s_date_start = SimpleDateFormat(Config.DATE_SHORT, Locale.US).parse(
                        str[5].trim { it <= ' ' }).time
                }
                var s_date_end: Long = 0
                if (str[6].trim { it <= ' ' }.length > 0) {
                    s_date_end = SimpleDateFormat(Config.DATE_SHORT, Locale.US).parse(
                        str[6].trim { it <= ' ' }).time
                    s_date_end += (24 * 60 * 60 * 1000 - 1).toLong() // vollen Tag setzen
                }
                var s_create_date: Long
                s_create_date = if (str.size > 7 && str[7].trim { it <= ' ' }.length > 0) {
                    SimpleDateFormat(Config.DATE_LONG, Locale.US).parse(
                        str[7].trim { it <= ' ' }).time
                } else {
                    Date().time
                }
                regularTransactionList.add(
                    RegularTransaction(
                        s_id,
                        s_month,
                        s_day,
                        s_description,
                        s_category,
                        s_amount,
                        s_date_start,
                        s_date_end,
                        s_create_date
                    )
                )
            } else {
                throw Exception(context.resources.getString(R.string.wrongLineFormat, line))
            }
        }
        br.close()
        val dbAdapter = DatabaseAdapter(context)
        dbAdapter.open()
        for (rt in regularTransactionList) {
            dbAdapter.insert(rt)
        }
        dbAdapter.close()
        return true
    }

    @JvmStatic
    @Throws(IOException::class)
    fun exportFileRegular(fileName: String, context: Context): String {
        val dbAdapter = DatabaseAdapter(context)
        dbAdapter.open()
        val regularTransactions = dbAdapter.allRegular
        val df1: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val df2: DateFormat = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val fileName = fileName + "_" + df1.format(Date()) + ".csv"
        val directory = Environment.getExternalStorageDirectory()
        val fileExport = File(directory, fileName)
        val out = FileWriter(fileExport)
        for (regularTransaction in regularTransactions) {
            out.append(java.lang.String.valueOf(regularTransaction.month))
                .append(Config.CSV_DELIMITER)
            out.append(java.lang.String.valueOf(regularTransaction.day))
                .append(Config.CSV_DELIMITER)
            out.append(regularTransaction.description).append(Config.CSV_DELIMITER)
            out.append(regularTransaction.category).append(Config.CSV_DELIMITER)
            out.append(java.lang.String.valueOf(regularTransaction.amount))
                .append(Config.CSV_DELIMITER)
            if (regularTransaction.date_start > 0) {
                out.append(df2.format(regularTransaction.date_start)).append(Config.CSV_DELIMITER)
            } else {
                out.append(Config.CSV_DELIMITER)
            }
            if (regularTransaction.date_end > 0) {
                out.append(df2.format(regularTransaction.date_end)).append(Config.CSV_DELIMITER)
            } else {
                out.append(Config.CSV_DELIMITER)
            }
            out.append(df1.format(regularTransaction.date_create)).append(Config.CSV_DELIMITER)
            out.append(Config.CSV_NEW_LINE)
        }
        out.close()
        dbAdapter.close()
        return fileExport.absolutePath
    }

    @JvmStatic
    @Throws(Exception::class)
    fun importFileTransaction(filename: String?, context: Context): Boolean {
        val transactionList: MutableList<Transaction> = ArrayList()
        val br = BufferedReader(InputStreamReader(FileInputStream(filename), Config.CSV_CODE_PAGE))

        // BOM marker will only appear on the very beginning
        br.mark(4)
        if ('\ufeff'.toInt() != br.read()) br.reset() // not the BOM marker
        var line: String
        while (br.readLine().also { line = it } != null) {
            val str = line.split(Config.CSV_DELIMITER).toTypedArray()
            if (str.size > 5) {
                val s_id: Long = 0
                val s_regular_id = java.lang.Long.valueOf(str[0])
                val s_description = str[1]
                val s_category = str[2]
                val s_date = SimpleDateFormat(Config.DATE_SHORT, Locale.US).parse(
                    str[3]
                ).time
                val s_amount_planned = java.lang.Double.valueOf(str[4].replace(',', '.'))
                val s_amount_fact = java.lang.Double.valueOf(str[5].replace(',', '.'))
                var s_create_date: Long
                s_create_date = if (str.size > 6 && str[6].trim { it <= ' ' }.length > 0) {
                    SimpleDateFormat(Config.DATE_LONG, Locale.US).parse(
                        str[6]
                    ).time
                } else {
                    Date().time
                }
                transactionList.add(
                    Transaction(
                        s_id,
                        s_regular_id,
                        s_description,
                        s_category,
                        s_date,
                        s_amount_planned,
                        s_amount_fact,
                        s_create_date
                    )
                )
            } else {
                throw Exception(context.resources.getString(R.string.wrongLineFormat, line))
            }
        }
        val dbAdapter = DatabaseAdapter(context)
        dbAdapter.open()
        for (transaction in transactionList) {
            dbAdapter.insert(transaction)
        }
        br.close()
        dbAdapter.close()
        return true
    }

    @JvmStatic
    @Throws(IOException::class)
    fun exportFileTransaction(fileName: String, context: Context): String {
        val dbAdapter = DatabaseAdapter(context)
        dbAdapter.open()
        val transactions = dbAdapter.getTransactions(context)
        val df1: DateFormat = SimpleDateFormat(Config.DATE_LONG, Locale.US)
        val df2: DateFormat = SimpleDateFormat(Config.DATE_SHORT, Locale.US)
        val fileName = fileName + "_" + df1.format(Date()) + ".csv"
        val directory = Environment.getExternalStorageDirectory()
        val fileExport = File(directory, fileName)
        val out = FileWriter(fileExport)
        for (transaction in transactions) {
            out.append(
                transaction.regular_id.toString() + Config.CSV_DELIMITER +
                        transaction.description + Config.CSV_DELIMITER +
                        transaction.category + Config.CSV_DELIMITER +
                        df2.format(transaction.date) + Config.CSV_DELIMITER + transaction.amount_planned.toString() + Config.CSV_DELIMITER + transaction.amount_fact.toString() + Config.CSV_DELIMITER +
                        df1.format(transaction.date_create) + Config.CSV_NEW_LINE
            )
        }
        out.close()
        dbAdapter.close()
        return fileExport.absolutePath
    }
}