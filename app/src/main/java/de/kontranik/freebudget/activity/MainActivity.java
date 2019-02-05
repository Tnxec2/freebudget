package de.kontranik.freebudget.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.adapter.TransactionAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Transaction;
import de.kontranik.freebudget.service.PlanRegular;

public class MainActivity extends AppCompatActivity {

    private ListView listView_Transactions;
    private TextView textView_Year, textView_Month;
    private TextView textView_receipts_planed, textView_spending_planed, textView_total_planed;
    private TextView textView_receipts_fact, textView_spending_fact, textView_total_fact;
    private Button btn_planRegular;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus, fab_add_plus_planed, fab_add_minus_planed;

    private int year, month;

    private String[] months;

    List<Transaction> transactionList = new ArrayList<>();
    TransactionAdapter transactionAdapter;

    Boolean isMove;

    public static final String TRANS_STAT = "TRANS_STAT";
    public static final String TRANS_STAT_PLUS = "plus";
    public static final String TRANS_STAT_MINUS = "minus";
    public static final String TRANS_TYP = "TRANS_TYP";
    public static final String TRANS_TYP_PLANED = "planed";
    public static final String TRANS_TYP_FACT= "fact";

    double amount_planed, amount_fact;
    double receipts_planed = 0;
    double receipts_fact = 0;
    double spending_planed = 0;
    double spending_fact = 0;
    double total_planed = 0;
    double total_fact = 0;

    boolean showOnlyPlaned = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_activity_main);
        listView_Transactions = findViewById(R.id.listView_transactions);
        textView_Month = findViewById(R.id.textView_Month);
        textView_Year = findViewById(R.id.textView_Year);

        textView_receipts_planed = findViewById(R.id.textView_receipts_planed);
        textView_receipts_fact = findViewById(R.id.textView_receipts_fact);
        textView_spending_planed = findViewById(R.id.textView_spending_planed);
        textView_spending_fact = findViewById(R.id.textView_spending_fact);
        textView_total_planed = findViewById(R.id.textView_total_planed);
        textView_total_fact = findViewById(R.id.textView_total_fact);

        btn_planRegular = findViewById(R.id.btn_planRegular);

        fab_add = findViewById(R.id.fab_add);
        fab_add_plus = findViewById(R.id.fab_add_plus);
        fab_add_minus = findViewById(R.id.fab_add_minus);
        fab_add_plus_planed = findViewById(R.id.fab_add_plus_planed);
        fab_add_minus_planed = findViewById(R.id.fab_add_minus_planed);

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
                editTransaction(entry);
            }
        });

        // set list adapter
        transactionAdapter = new TransactionAdapter(this,
                R.layout.layout_transaction_item,
                transactionList);

        // set adapter
        listView_Transactions.setAdapter(transactionAdapter);
        // Register the ListView  for Context menu
        registerForContextMenu(listView_Transactions);


        fab_add.setOnTouchListener(new View.OnTouchListener () {
            public boolean onTouch (View view, MotionEvent motionEvent){
                isMove = false;
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    ClipData data = ClipData.newPlainText("", "");
                    fab_add.setImageResource(R.drawable.ic_euro_symbol_white_24dp);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        view.startDragAndDrop(data, shadowBuilder, view, 0);
                    } else {
                        //noinspection deprecation
                        view.startDrag(data, shadowBuilder, view, 0);
                    }
                    fab_add_plus.setVisibility(View.VISIBLE);
                    fab_add_minus.setVisibility(View.VISIBLE);
                    fab_add_plus_planed.setVisibility(View.VISIBLE);
                    fab_add_minus_planed.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.INVISIBLE);
                    isMove = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(!isMove) {
                        add(TRANS_STAT_MINUS, TRANS_TYP_FACT);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fab_add_plus.setVisibility(View.VISIBLE);
                    fab_add_minus.setVisibility(View.VISIBLE);
                    fab_add_plus_planed.setVisibility(View.VISIBLE);
                    fab_add_minus_planed.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        listView_Transactions.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        setNormalStat();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        setNormalStat();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        fab_add_plus.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        add(TRANS_STAT_PLUS, TRANS_TYP_FACT);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        fab_add_minus.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        add(TRANS_STAT_MINUS, TRANS_TYP_FACT);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        fab_add_plus_planed.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        add(TRANS_STAT_PLUS, TRANS_TYP_PLANED);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        fab_add_minus_planed.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        add(TRANS_STAT_MINUS, TRANS_TYP_PLANED);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // getTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.app_bar_switch);
        item.setActionView(R.layout.switch_item);

        final Switch mySwitch = item.getActionView().findViewById(R.id.switch_id);
        mySwitch.setText(R.string.all);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something based on isChecked
                showOnlyPlaned = isChecked;

                if ( showOnlyPlaned) {
                    mySwitch.setText(R.string.only_planed);
                } else {
                    mySwitch.setText(R.string.all);
                }

                getTransactions();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.action_all_entry :
                intent = new Intent(this, ManagePlanedTransactionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_manage_regular :
                intent = new Intent( this, ManageRegularTransactionActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getTransactions();
    }

    public void getTransactions () {

        DatabaseAdapter dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        /*
         * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
         * muss diese Liste hier zuerst geputzt werden
         * und danach neue Liste ge-added werden
         */

        transactionList.clear();
        //transactionList.addAll( dbAdapter.getTransactions(this.year, this.month, this.showOnlyPlaned) );

        List<Transaction> dbTransactions = dbAdapter.getTransactions(this.year, this.month, false) ;

        clearSummen();

        for (Transaction transaction: dbTransactions) {
            amount_planed = transaction.getAmount_planed();
            if (amount_planed > 0) receipts_planed += amount_planed;
            else spending_planed += Math.abs(amount_planed);
            total_planed = total_planed + amount_planed;

            amount_fact = transaction.getAmount_fact();
            if (amount_fact > 0) receipts_fact += amount_fact;
            else spending_fact += Math.abs(amount_fact);
            total_fact += amount_fact;

            /*
             * bereits gebuchte Transactionen überlesen
             */
            if ( !showOnlyPlaned || transaction.getAmount_fact() == 0 ) {
                transactionList.add(transaction);
            }
        }

        setSummen();

        dbAdapter.close();

        //
        if ( transactionList.size() == 0) {
            btn_planRegular.setVisibility(View.VISIBLE);
        } else {
            btn_planRegular.setVisibility(View.GONE);
        }

        // und den Adapter aktualisieren
        transactionAdapter.notifyDataSetChanged();
    }

    public void prevMonth(View view){
        prevMonth();
    }

    public void nextMonth(View view){
        nextMonth();
    }

    public void prevMonth(){
        if (this.month == 1 ) {
            if ( this.year == 2000 ) return;
            this.month = 12;
            this.year--;
        } else {
            this.month = this.month - 1;
        }
        setMonthTextView();
        this.getTransactions();
    }

    public void nextMonth(){
        if (this.month == 12 ) {
            if ( this.year == 3000 ) return;
            this.month = 1;
            this.year++;
        } else {
            this.month = this.month + 1;
        }
        setMonthTextView();
        this.getTransactions();
    }

    private void setMonthTextView() {
        this.textView_Month.setText(String.format(Locale.getDefault(),"%d / %s", this.year, this.months[this.month]));
    }

    public void planRegular(View view) {
        PlanRegular.setRegularToPlaned(this, year, month);
        getTransactions();
    }

    public void add(String transStat, String planed){
        setNormalStat();
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra(TRANS_STAT, transStat);
        intent.putExtra(TRANS_TYP, planed);
        startActivity(intent);
    }

    private void setNormalStat(){
        fab_add.setImageResource(R.drawable.ic_add_white_24dp);
        fab_add.setVisibility(View.VISIBLE);
        fab_add_plus.setVisibility(View.INVISIBLE);
        fab_add_minus.setVisibility(View.INVISIBLE);
        fab_add_plus_planed.setVisibility(View.INVISIBLE);
        fab_add_minus_planed.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transaction_list_popup_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;

        Transaction transaction = transactionList.get(listPosition);
        if(item.getItemId()==R.id.popup_edit){
            editTransaction(transaction);
        }
        else if(item.getItemId()==R.id.popup_delete){
            deleteTransaction(transaction);
        }else{
            return false;
        }
        return true;
    }

    private void editTransaction(Transaction entry) {
        if(entry!=null) {
            Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
            intent.putExtra("id", entry.getId());
            intent.putExtra("click", 25);
            startActivity(intent);
        }
    }

    private void deleteTransaction(Transaction entry) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();
        databaseAdapter.deleteTransaction(entry.getId());
        databaseAdapter.close();
        getTransactions();
    }

    private void setSummen() {
        textView_spending_planed.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_planed));
        textView_spending_planed.setTextColor(ContextCompat.getColor(this, R.color.colorRed));

        textView_spending_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_fact));
        textView_spending_fact.setTextColor(ContextCompat.getColor(this, R.color.colorRed));

        textView_receipts_planed.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_planed));
        textView_receipts_planed.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));

        textView_receipts_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_fact));
        textView_receipts_fact.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));

        textView_total_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", total_fact));
        textView_total_planed.setText(String.format(Locale.getDefault(),"%1$,.2f", total_planed));

        if (total_planed > 0) {
            textView_total_planed.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        } else {
            textView_total_planed.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }
        if (total_fact > 0) {
            textView_total_fact.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        } else {
            textView_total_fact.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }
    }

    private void clearSummen() {
        amount_planed = 0;
        amount_fact = 0;
        total_planed = 0;
        total_fact = 0;
        receipts_fact = 0;
        receipts_planed = 0;
        spending_fact = 0;
        spending_planed = 0;
    }
}

