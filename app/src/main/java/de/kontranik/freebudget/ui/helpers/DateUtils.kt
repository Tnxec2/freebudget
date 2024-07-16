package de.kontranik.freebudget.ui.helpers

import java.text.DateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class DateUtils {
    companion object {
        private fun getCalendar(): Calendar {
            return GregorianCalendar.getInstance(Locale.getDefault())
        }

        private fun getDateFormater(): DateFormat {
            return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        }

        fun getDateMedium(milliSeconds: Long): String {
            return getDateFormater().format(milliSeconds)
        }

        fun now(): Long {
            return getCalendar().timeInMillis
        }

        fun getYear(): Int {
            return getCalendar()[Calendar.YEAR]
        }

        fun getMonth(): Int {
            return getCalendar()[Calendar.MONTH] + 1
        }
    }
}