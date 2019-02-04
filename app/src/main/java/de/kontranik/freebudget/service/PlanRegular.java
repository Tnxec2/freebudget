package de.kontranik.freebudget.service;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;
import de.kontranik.freebudget.model.Transaction;

public class PlanRegular {

    public static void setRegularToPlaned(Context context, int year, int month) {

        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();

        /*
         *     aktuellen Monat und allgemeine (Monat == 0)
         */
        List<RegularTransaction> regularTransactionList = dbAdapter.getRegular(month);
        regularTransactionList.addAll(dbAdapter.getRegular(0));

        for ( RegularTransaction rt : regularTransactionList ) {
            if (!dbAdapter.checkTransactions(year, month, rt.getId())) {
                Calendar cal = new GregorianCalendar(year, month - 1, rt.getDay(), 0, 0, 1);

                Transaction transaction = new Transaction(0, rt.getId(), rt.getDescription(), rt.getCategory(), cal.getTimeInMillis(), rt.getAmount(), 0);
                dbAdapter.insert(transaction);
            }
        }
    }
}

