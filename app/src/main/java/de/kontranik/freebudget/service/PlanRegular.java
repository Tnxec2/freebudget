package de.kontranik.freebudget.service;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;
import de.kontranik.freebudget.model.Transaction;

public class PlanRegular {

    public static void setRegularToPlanned(Context context, int year, int month) {

        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();

        /*
         *     aktuellen Monat und allgemeine (Monat == 0)
         */
        List<RegularTransaction> regularTransactionList = dbAdapter.getRegular(month);
        regularTransactionList.addAll(dbAdapter.getRegular(0));

        for ( RegularTransaction rt : regularTransactionList ) {
            if (!dbAdapter.checkTransactions(year, month, rt.getId())) {
                /*
                 * prüfen, wenn Tag grösser als aktueller monat zulässt,
                 * dann auf letzen tag des monats setzen
                 */
                Calendar cal = new GregorianCalendar(year, month - 1, 1, 0, 0, 1);
                int i = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if ( rt.getDay() > i ) rt.setDay(i);

                cal = new GregorianCalendar(year, month - 1, rt.getDay(), 0, 0, 1);

                /*
                 * inserten nur wenn Datum im Rahmen von START-END oder START-END nicht eingegeben sind
                 */
                if (
                        ( rt.getDate_start() > 0 && cal.getTimeInMillis() >= rt.getDate_start() )
                    ||
                        ( rt.getDate_end() > 0 && cal.getTimeInMillis() <= rt.getDate_end() )
                    ||
                        ( rt.getDate_start() == 0 && rt.getDate_end() == 0 ) ) {
                    Transaction transaction = new Transaction(0, rt.getId(), rt.getDescription(), rt.getCategory(), cal.getTimeInMillis(), rt.getAmount(), 0);
                    dbAdapter.insert(transaction);
                }
            }
        }
    }
}

