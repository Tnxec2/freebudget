package de.kontranik.freebudget.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.config.Config.CSV_CODE_PAGE;
import static de.kontranik.freebudget.config.Config.CSV_DELIMITER;
import static de.kontranik.freebudget.config.Config.CSV_NEW_LINE;
import static de.kontranik.freebudget.config.Config.DATE_LONG;
import static de.kontranik.freebudget.config.Config.DATE_SHORT;

public class FileService {

    public static boolean importFileRegular(String filename, Context context) throws Exception {

        List<RegularTransaction> regularTransactionList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), CSV_CODE_PAGE));
        String line;

        // BOM marker will only appear on the very beginning
        br.mark(4);
        if ('\ufeff' != br.read()) br.reset(); // not the BOM marker

        while ((line = br.readLine()) != null) {
            String[] str = line.split(CSV_DELIMITER);
            Log.d("NIK", line);
            if (str.length > 4) {
                long s_id = 0;
                int s_month = Integer.valueOf(str[0].trim());
                if (s_month < 0 || s_month > 12) throw new Exception( context.getResources().getString(R.string.wrongMonthInTheLine, line));
                int s_day = Integer.valueOf(str[1].trim());
                if (s_day < 1 || s_day > 31) throw new Exception(context.getResources().getString(R.string.wrongDayInTheLine, line));
                String s_description = str[2].trim();
                String s_category = str[3].trim();
                double s_amount = Double.valueOf(str[4].trim().replace(',', '.'));

                long s_create_date;
                if (str.length > 5 && str[5].trim().length() > 0) {
                    s_create_date = new SimpleDateFormat(DATE_LONG, Locale.US).parse(str[5].trim()).getTime();
                } else {
                    s_create_date = new Date().getTime();
                }
                regularTransactionList.add( new RegularTransaction(s_id, s_month, s_day, s_description, s_category, s_amount, s_create_date));
            } else {
                throw new Exception(context.getResources().getString(R.string.wrongLineFormat, line));
            }
        }
        br.close();
        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();
        for(RegularTransaction rt: regularTransactionList) {
            dbAdapter.insert(rt);
        }
        dbAdapter.close();
        return true;
    }

    public static boolean exportFileRegular(String fileName, Context context) throws IOException {
        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();

        List<RegularTransaction> regularTransactions = dbAdapter.getAllRegular();

        DateFormat df1 = new SimpleDateFormat(DATE_LONG, Locale.US);
        DateFormat df2 = new SimpleDateFormat(DATE_SHORT, Locale.US);

        String FILENAME = fileName + "_" + df1.format(new Date()) + ".csv";
        File directory = Environment.getExternalStorageDirectory();
        File file_export = new File(directory, FILENAME);

        FileWriter out = new FileWriter(file_export);

        for (RegularTransaction regularTransaction: regularTransactions) {

            out.append(
                String.valueOf(regularTransaction.getMonth()) + CSV_DELIMITER +
                String.valueOf(regularTransaction.getDay()) + CSV_DELIMITER +
                regularTransaction.getDescription() + CSV_DELIMITER +
                regularTransaction.getCategory() + CSV_DELIMITER +
                String.valueOf(regularTransaction.getAmount()) + CSV_DELIMITER +
                df1.format(regularTransaction.getDate_create()) + CSV_NEW_LINE
            );
        }

        out.close();
        dbAdapter.close();
        return true;
    }

    public static boolean importFileTransaction(String filename, Context context) throws Exception {

        List<Transaction> transactionList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), CSV_CODE_PAGE));

        // BOM marker will only appear on the very beginning
        br.mark(4);
        if ('\ufeff' != br.read()) br.reset(); // not the BOM marker

        String line;

        while ((line = br.readLine()) != null) {
            String[] str = line.split(CSV_DELIMITER);
            if (str.length > 5) {
                long s_id = 0;
                long s_regular_id = Long.valueOf(str[0]);
                String s_description = str[1];
                String s_category = str[2];
                long s_date = new SimpleDateFormat(DATE_SHORT, Locale.US).parse(str[3]).getTime();
                double s_amount_planed = Double.valueOf(str[4].replace(',', '.'));
                double s_amount_fact = Double.valueOf(str[5].replace(',', '.'));
                long s_create_date;
                if (str.length > 6 && str[6].trim().length() > 0) {
                    s_create_date = new SimpleDateFormat(DATE_LONG, Locale.US).parse(str[6]).getTime();
                } else {
                    s_create_date = new Date().getTime();
                }
                transactionList.add( new Transaction(s_id, s_regular_id, s_description, s_category, s_date, s_amount_planed, s_amount_fact, s_create_date));
            } else {
                throw new Exception(context.getResources().getString(R.string.wrongLineFormat, line));
            }
        }
        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();
        for(Transaction transaction: transactionList) {
            dbAdapter.insert(transaction);
        }
        br.close();
        dbAdapter.close();
        return true;
    }

    public static boolean exportFileTransaction(String fileName, Context context) throws IOException {
        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        dbAdapter.open();

        List<Transaction> transactions = dbAdapter.getTransactions(context);

        DateFormat df1 = new SimpleDateFormat(DATE_LONG, Locale.US);
        DateFormat df2 = new SimpleDateFormat(DATE_SHORT, Locale.US);

        String FILENAME = fileName + "_" + df1.format(new Date())+ ".csv";
        File directory = Environment.getExternalStorageDirectory();
        File file_export = new File(directory, FILENAME);

        FileWriter out = new FileWriter(file_export);

        for (Transaction transaction: transactions) {
            out.append(
                String.valueOf(transaction.getRegular_id()) + CSV_DELIMITER +
                transaction.getDescription() + CSV_DELIMITER +
                transaction.getCategory() + CSV_DELIMITER +
                df2.format(transaction.getDate()) + CSV_DELIMITER +
                String.valueOf(transaction.getAmount_planed()) + CSV_DELIMITER +
                String.valueOf(transaction.getAmount_fact()) + CSV_DELIMITER +
                df1.format(transaction.getDate_create()) + CSV_NEW_LINE
            );
        }

        out.close();
        dbAdapter.close();
        return true;
    }
}
