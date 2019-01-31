package de.kontranik.freebudget.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.OnSwipeTouchListener;
import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;

import static de.kontranik.freebudget.activity.RegularTransactionActivity.MONTH;
import static de.kontranik.freebudget.activity.RegularTransactionActivity.TRANS_STAT;

public class ManageRegularTransactionActivity extends AppCompatActivity {

    private ListView listView_Transactions;
    private TextView textView_Month;
    private TextView textView_receipts, textView_spending, textView_total;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;
    private int month;
    private String[] months;
    RegularTransactionAdapter transactionAdapter;
    static String transStat;
    Boolean isMove;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_regular_transaction);

        setTitle(R.string.manage_regular);

        listView_Transactions = (ListView)findViewById(R.id.listView_regular_transactions);
        textView_Month = (TextView)findViewById(R.id.textView_Month_Regular);

        textView_receipts = (TextView)findViewById(R.id.textView_receipts_regular);
        textView_spending = (TextView)findViewById(R.id.textView_spending_regular);
        textView_total = (TextView)findViewById(R.id.textView_total_regular);

        fab_add = (FloatingActionButton)findViewById(R.id.fab_add_regular);
        fab_add_plus = (FloatingActionButton)findViewById(R.id.fab_add_plus_regular);
        fab_add_minus = (FloatingActionButton)findViewById(R.id.fab_add_minus_regular);

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainlayout_regular);

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();
        month = date.get(Calendar.MONTH)+1;
        textView_Month.setText(this.months[month]);

        listView_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegularTransaction entry = transactionAdapter.getItem(position);
                if(entry!=null) {
                    Intent intent = new Intent(getApplicationContext(), RegularTransactionActivity.class);
                    intent.putExtra("id", entry.getId());
                    intent.putExtra("click", 25);
                    startActivity(intent);
                }
            }
        });

        listView_Transactions.setOnTouchListener(new OnSwipeTouchListener(ManageRegularTransactionActivity.this){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

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
                    fab_add.setVisibility(View.INVISIBLE);
                    isMove = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(!isMove) {
                        transStat = "minus";
                        add();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fab_add_plus.setVisibility(View.VISIBLE);
                    fab_add_minus.setVisibility(View.VISIBLE);
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
                        // do nothing
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
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        transStat = "plus";
                        add();
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
                        transStat = "minus";
                        add();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        getTransactions();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        List<RegularTransaction> transactions = adapter.getTransactions(month);

        // set list adapter
        transactionAdapter = new RegularTransactionAdapter(this,
                R.layout.layout_transaction_item,
                transactions);
        // set adapter
        listView_Transactions.setAdapter(transactionAdapter);

        adapter.close();
    }

    public void prevMonth(View view){
        prevMonth();
    }

    public void nextMonth(View view){
        nextMonth();
    }

    public void prevMonth(){
        if (this.month == 0 ) return;
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

    public void getTransactions () {

        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        List<RegularTransaction> transactions = adapter.getTransactions(this.month);

        // set list adapter
        transactionAdapter = new RegularTransactionAdapter(this,
                R.layout.layout_regular_transaction_item,
                transactions);

        double cost;
        double receipts = 0;
        double spending = 0;
        double total = 0;
        for (RegularTransaction transaction: transactions) {
            cost = transaction.getAmount();
            if (cost > 0) {
                receipts += cost;
            } else {
                spending += Math.abs(cost);
            }
            total += cost;
        }

        textView_spending.setText(String.format(Locale.getDefault(),"%1$,.2f", spending));
        textView_spending.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        textView_receipts.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts));
        textView_receipts.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
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

    public void add(View view){
        Intent intent = new Intent(this, RegularTransactionActivity.class);
        startActivity(intent);
    }

    public void add(){
        setNormalStat();
        Intent intent = new Intent(this, RegularTransactionActivity.class);
        intent.putExtra(TRANS_STAT, transStat);
        intent.putExtra( MONTH, this.month);
        startActivity(intent);
    }

    private void setNormalStat(){
        fab_add.setImageResource(R.drawable.ic_add_white_24dp);
        fab_add.setVisibility(View.VISIBLE);
        fab_add_plus.setVisibility(View.INVISIBLE);
        fab_add_minus.setVisibility(View.INVISIBLE);
    }
}
