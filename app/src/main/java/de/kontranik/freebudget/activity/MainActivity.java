package de.kontranik.freebudget.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.OnSwipeTouchListener;
import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Transaction;

public class MainActivity extends AppCompatActivity {

    private ListView listView_Transactions;
    private TextView textView_Year, textView_Month;
    private TextView textView_receipts, textView_spending, textView_total;
    private int year, month;
    private String[] months;
    TransactionAdapter transactionAdapter;
    static final int RESULT_OPEN_FILENAME = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_activity_main);
        listView_Transactions = (ListView)findViewById(R.id.listView_transactions);
        textView_Month = (TextView)findViewById(R.id.textView_Month);
        textView_Year = (TextView)findViewById(R.id.textView_Year);

        textView_receipts = (TextView)findViewById(R.id.textView_receipts);
        textView_spending = (TextView)findViewById(R.id.textView_spending);
        textView_total = (TextView)findViewById(R.id.textView_total);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainlayout);

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH)+1;
        textView_Year.setText(String.format(Locale.getDefault(),"%d", year));
        textView_Month.setText(this.months[month]);

        listView_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction entry = transactionAdapter.getItem(position);
                if(entry!=null) {
                    Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                    intent.putExtra("id", entry.getId());
                    intent.putExtra("click", 25);
                    startActivity(intent);
                }
            }
        });

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.action_all_entry :
                intent = new Intent(this, AllTransactionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_manage_regular :
                intent = new Intent( this, ManageRegularTransactionActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_import_csv :
                Intent open_import = new Intent(this, OpenFileActivity.class);
                this.startActivityForResult(open_import, RESULT_OPEN_FILENAME);
                return true;
            case R.id.action_export_csv :
                DatabaseAdapter adapter = new DatabaseAdapter(this);
                adapter.open();

                List<Transaction> transactions = adapter.getTransactions(this);

                DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());

                String FILENAME = "export_" + df2.format(new Date())+ ".csv";
                File directory = Environment.getExternalStorageDirectory();
                File file_export = new File(directory, FILENAME);
                try {
                    FileWriter out = new FileWriter(file_export);

                    for (Transaction tr: transactions) {
                        // long id,
                        // String name,
                        // String type,
                        // double cost,
                        // String freq,
                        // int year,
                        // int month,
                        // long date
                        out.append(
                                    tr.getDescription() + ";" +
                                    String.valueOf(tr.getAmount_fact()) + ";" +
                                    String.valueOf(tr.getYear()) + ";" +
                                    String.valueOf(tr.getMonth()) + ";" +
                                    String.valueOf(tr.getDate_fact()) + "\n"
                        );
                    }

                    out.close();
                    adapter.close();
                    Toast.makeText(getApplicationContext(),
                            "export OK to file:\n " +  file_export,
                            Toast.LENGTH_LONG).show();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "export error.\n" + e ,
                            Toast.LENGTH_LONG).show();
                    Log.d("NIK", e.toString());
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getTransactions();
    }

    public void getTransactions () {

        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        List<Transaction> transactions = adapter.getTransactions(this.year, this.month);

        // set list adapter
        transactionAdapter = new TransactionAdapter(this,
                                            R.layout.layout_transaction_item,
                                            transactions);

        double cost;
        double receipts = 0;
        double spending = 0;
        double total = 0;
        for (Transaction transaction: transactions) {
            cost = transaction.getAmount_fact();
            if (cost > 0) {
                receipts += cost;
            } else {
                spending += Math.abs(cost);
            }
            total += cost;
        }

        textView_spending.setText(String.format(Locale.getDefault(),"%1$,.2f", spending));
        textView_receipts.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        textView_receipts.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts));
        textView_spending.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        textView_total.setText(String.format(Locale.getDefault(),"%1$,.2f", total));
        if (total > 0) {
            textView_total.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        } else {
            textView_total.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }

        // set adapter
        listView_Transactions.setAdapter(transactionAdapter);
        adapter.close();
    }

    public void prevYear(View view){
        if (this.year == 2000 ) return;
        this.year = this.year - 1;
        this.textView_Year.setText(String.format(Locale.getDefault(),"%d", this.year));
        this.getTransactions();
    }

    public void nextYear(View view){
        if (this.year == 2100 ) return;
        this.year = this.year + 1;
        this.textView_Year.setText(String.format(Locale.getDefault(),"%d", this.year));
        this.getTransactions();
    }

    public void prevMonth(View view){
        prevMonth();
    }

    public void nextMonth(View view){
        nextMonth();
    }

    public void prevMonth(){
        if (this.month == 1 ) return;
        this.month = this.month - 1;
        this.textView_Month.setText(this.months[this.month]);
        this.getTransactions();
    }
    public void nextMonth(){
        if (this.month == 12 ) return;
        this.month = this.month + 1;
        this.textView_Month.setText(this.months[this.month]);
        this.getTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int s_id ;
        String s_name;
        Double s_cost ;
        String s_freq ;
        Long s_date ;
        int s_year;
        int s_month;
        boolean import_status = true;
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        switch (requestCode) {
            case RESULT_OPEN_FILENAME:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra("fileName");
                    //String shortFileName = data.getStringExtra("shortFileName");

                    Toast.makeText(this,
                            "import to file: " + fileName,
                            Toast.LENGTH_SHORT).show();

                    FileReader file;
                    try {
                        file = new FileReader(fileName);
                        BufferedReader buffer = new BufferedReader(file);
                        String line ;
                        adapter.open();
                        while (( line = buffer.readLine()) != null) {
                            String[] str = line.split(";");
                            if (str.length == 7) {
                                Transaction transaction;
                                // long id,
                                // String name,
                                // String type,
                                // double cost,
                                // String freq,
                                // int year,
                                // int month,
                                // long date
                                 try {
                                    s_id = 0;
                                    s_name = str[0];
                                    s_cost = Double.valueOf(str[2].replace(',','.'));
                                    s_freq = str[3];
                                     if( !s_freq.equals("m") && !s_freq.equals("y") && !s_freq.equals("o") ) continue; //fehler
                                    s_year = Integer.valueOf(str[4]);
                                     if(s_year < 0) continue; //fehler
                                    s_month = Integer.valueOf(str[5]);
                                     if(s_month < 0 || s_month > 12) continue; //fehler
                                    s_date = Long.valueOf(str[6]);

                                    /*
                                    transaction = new Transaction(
                                        ???
                                    );
                                    adapter.insert(transaction);
                                    */
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "File read error\n" + e.toString() ,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "File read error. Line:\n" + line ,
                                        Toast.LENGTH_SHORT).show();
                                import_status = false;
                            }
                        }
                        adapter.close();
                        if (import_status) {
                            Toast.makeText(getApplicationContext(),
                                    "Import ok",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Import fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"File read error: " + fileName +
                                ". Format: name; type (r / o); cost; freq (o / m / y); year; month; date (0)",
                                Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }
}

