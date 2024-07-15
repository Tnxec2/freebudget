package de.kontranik.freebudget.ui.helpers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateUtils {
    companion object {
        fun getDate(milliSeconds: Long): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance();
            calendar.timeInMillis = milliSeconds;
            return formatter.format(calendar.time);
        }
    }
}