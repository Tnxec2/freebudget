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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.service.FileService;
import de.kontranik.freebudget.service.OnSwipeTouchListener;
import de.kontranik.freebudget.R;
import de.kontranik.freebudget.adapter.RegularTransactionAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;

import static de.kontranik.freebudget.activity.OpenFileActivity.RESULT_FILENAME;
import static de.kontranik.freebudget.activity.RegularTransactionActivity.MONTH;
import static de.kontranik.freebudget.activity.RegularTransactionActivity.TRANS_STAT;

public class ManageRegularTransactionActivity extends AppCompatActivity {

    static final int RESULT_OPEN_FILENAME = 234;

    private ListView listView_Transactions;
    private TextView textView_Month;
    private TextView textView_receipts, textView_spending, textView_total;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;

    private int month;
    private String[] months;
    static String transStat;
    Boolean isMove;

    List<RegularTransaction> transactionList = new ArrayList<>();
    RegularTransactionAdapter transactionAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_regular_transaction);

        setTitle(R.string.manage_regular);

        listView_Transactions = findViewById(R.id.listView_regular_transactions);
        textView_Month = findViewById(R.id.textView_Month_Regular);

        textView_receipts = findViewById(R.id.textView_receipts_regular);
        textView_spending = findViewById(R.id.textView_spending_regular);
        textView_total = findViewById(R.id.textView_total_regular);

        fab_add = findViewById(R.id.fab_add_regular);
        fab_add_plus = findViewById(R.id.fab_add_plus_regular);
        fab_add_minus = findViewById(R.id.fab_add_minus_regular);

        ConstraintLayout mainLayout = findViewById(R.id.mainlayout_regular);

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

        // set list adapter
        transactionAdapter = new RegularTransactionAdapter(this,
                R.layout.layout_regular_transaction_item,
                transactionList);
        // set adapter
        listView_Transactions.setAdapter(transactionAdapter);

        mainLayout.setOnTouchListener(new OnSwipeTouchListener(ManageRegularTransactionActivity.this){
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.manageregulartransaction_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_import_csv :
                Intent open_import = new Intent(this, OpenFileActivity.class);
                this.startActivityForResult(open_import, RESULT_OPEN_FILENAME);
                return true;
            case R.id.action_export_csv :
                try {
                    FileService.exportFileRegular("export_freebudget_regular_transaction", this);
                } catch (IOException e) {
                    //e.printStackTrace();
                    Toast.makeText(this, this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                            Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        getTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_OPEN_FILENAME:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);

                    Toast.makeText(this,
                            this.getResources().getString(R.string.importFromFile, fileName),
                            Toast.LENGTH_SHORT).show();

                    try {
                        FileService.importFileRegular(fileName, this);
                        this.getTransactions();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, this.getResources().getString(R.string.importFail, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
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

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();

        transactionList.clear();
        transactionAdapter.clear();

        transactionList.addAll( databaseAdapter.getRegular(this.month) );

        double amount;
        double receipts = 0;
        double spending = 0;
        double total = 0;
        for (RegularTransaction transaction: transactionList) {
            amount = transaction.getAmount();
            if (amount > 0) {
                receipts += amount;
            } else {
                spending += Math.abs(amount);
            }
            total += amount;
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

        databaseAdapter.close();

        transactionAdapter.notifyDataSetChanged();
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
