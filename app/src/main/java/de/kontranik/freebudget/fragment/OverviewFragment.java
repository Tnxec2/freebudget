package de.kontranik.freebudget.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.activity.TransactionActivity;
import de.kontranik.freebudget.adapter.TransactionAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Transaction;
import de.kontranik.freebudget.service.OnSwipeTouchListener;
import de.kontranik.freebudget.service.PlanRegular;

import static de.kontranik.freebudget.service.Constant.TRANS_ID;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_MINUS;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_PLUS;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_FACT;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_PLANNED;

public class OverviewFragment extends Fragment implements View.OnClickListener {

    private static final String PREFS_KEY_LISTPOSITION = "LISTPOS";

    private ListView listView_Transactions;
    private TextView textView_Month;
    private TextView textView_receipts_planned, textView_spending_planned, textView_total_planned;
    private TextView textView_receipts_fact, textView_spending_fact, textView_total_fact;
    private Button btn_planRegular;
    private ImageButton btn_prevMonth, btn_nextMonth;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus, fab_add_plus_planned, fab_add_minus_planned;

    private int year, month;

    private String[] months;

    List<Transaction> transactionList = new ArrayList<>();
    TransactionAdapter transactionAdapter;

    Boolean isMove;

    double amount_planned, amount_fact;
    double receipts_planned = 0;
    double receipts_fact = 0;
    double spending_planned = 0;
    double spending_fact = 0;
    double total_planned = 0;
    double total_fact = 0;

    boolean showOnlyPlanned = false;

    public static long lastEditedId = 0;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_overview, parent, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView_Transactions = (ListView) view.findViewById(R.id.listView_transactions);
        textView_Month = (TextView) view.findViewById(R.id.textView_Month);

        textView_receipts_planned = (TextView) view.findViewById(R.id.textView_receipts_planned);
        textView_receipts_fact = (TextView) view.findViewById(R.id.textView_receipts_fact);
        textView_spending_planned = (TextView) view.findViewById(R.id.textView_spending_planned);
        textView_spending_fact = (TextView) view.findViewById(R.id.textView_spending_fact);
        textView_total_planned = (TextView) view.findViewById(R.id.textView_total_planned);
        textView_total_fact = (TextView) view.findViewById(R.id.textView_total_fact);

        btn_planRegular = (Button) view.findViewById(R.id.btn_planRegular);

        btn_prevMonth = (ImageButton) view.findViewById(R.id.btn_prevMonth);
        btn_nextMonth = (ImageButton) view.findViewById(R.id.btn_nextMonth);

        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab_add_plus = (FloatingActionButton) view.findViewById(R.id.fab_add_plus);
        fab_add_minus = (FloatingActionButton) view.findViewById(R.id.fab_add_minus);
        fab_add_plus_planned = (FloatingActionButton) view.findViewById(R.id.fab_add_plus_planned);
        fab_add_minus_planned = (FloatingActionButton) view.findViewById(R.id.fab_add_minus_planned);

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH)+1;

        setMonthTextView();

        btn_planRegular.setOnClickListener(this);
        btn_prevMonth.setOnClickListener(this);
        btn_nextMonth.setOnClickListener(this);

        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.linearLayout1);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

        listView_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction entry = transactionAdapter.getItem(position);
                editTransaction(entry, TRANS_TYP_FACT);
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

        // set list adapter
        transactionAdapter = new TransactionAdapter(view.getContext(), R.layout.layout_transaction_item, transactionList);

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
                    fab_add_plus_planned.setVisibility(View.VISIBLE);
                    fab_add_minus_planned.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.INVISIBLE);
                    isMove = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(!isMove) {
                        add(TRANS_STAT_MINUS, TRANS_TYP_FACT);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fab_add_plus.setVisibility(View.VISIBLE);
                    fab_add_minus.setVisibility(View.VISIBLE);
                    fab_add_plus_planned.setVisibility(View.VISIBLE);
                    fab_add_minus_planned.setVisibility(View.VISIBLE);
                    fab_add.setVisibility(View.VISIBLE);
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

        fab_add_plus_planned.setOnDragListener(new View.OnDragListener() {
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
                        add(TRANS_STAT_PLUS, TRANS_TYP_PLANNED);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        fab_add_minus_planned.setOnDragListener(new View.OnDragListener() {
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
                        add(TRANS_STAT_MINUS, TRANS_TYP_PLANNED);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getTransactions();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.transaction_list_popup_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;

        Transaction transaction = transactionList.get(listPosition);
        if(item.getItemId()==R.id.popup_edit){
            editTransaction(transaction, TRANS_TYP_FACT);
        } else if (item.getItemId()==R.id.popup_edit_planned) {
            editTransaction(transaction, TRANS_TYP_PLANNED);
        } else if(item.getItemId()==R.id.popup_delete){
            deleteTransaction(transaction);
        }else{
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_planRegular:
                planRegular();
                break;
            case R.id.btn_prevMonth:
                prevMonth();
                break;
            case R.id.btn_nextMonth:
                nextMonth();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState!=null) {
            outState.putInt(PREFS_KEY_LISTPOSITION, listView_Transactions.getFirstVisiblePosition());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState!=null) {
            int listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION);
            listView_Transactions.setSelection(listpos);
        }
    }

    public void getTransactions () {

        DatabaseAdapter dbAdapter = new DatabaseAdapter(getContext());
        dbAdapter.open();

        /*
         * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
         * muss diese Liste hier zuerst geputzt werden
         * und danach neue Liste ge-added werden
         */

        transactionList.clear();
        //transactionList.addAll( dbAdapter.getTransactions(this.year, this.month, this.showOnlyPlanned) );

        // als erstes komplett alle bewegungen für den Monat lesen
        List<Transaction> dbTransactions = dbAdapter.getTransactions(getContext(), this.year, this.month, false) ;

        clearSummen();

        lastEditedId = 0;
        long lastEditDate = 0;

        for (Transaction transaction: dbTransactions) {
            amount_planned = transaction.getAmount_planned();
            if (amount_planned > 0) receipts_planned += amount_planned;
            else spending_planned += Math.abs(amount_planned);
            total_planned = total_planned + amount_planned;

            amount_fact = transaction.getAmount_fact();
            if (amount_fact > 0) receipts_fact += amount_fact;
            else spending_fact += Math.abs(amount_fact);
            total_fact += amount_fact;

            /*
             * bereits gebuchte Transactionen überlesen
             */
            if ( !showOnlyPlanned || transaction.getAmount_fact() == 0 ) {
                transactionList.add(transaction);

                if ( transaction.getAmount_fact() != 0 && transaction.getDate_edit() > lastEditDate ) {
                    lastEditDate = transaction.getDate_edit();
                    lastEditedId = transaction.getId();
                }
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

    private void planRegular() {
        PlanRegular.setRegularToPlanned(getContext(), year, month);
        getTransactions();
    }

    private void add(String transStat, String planned){
        setNormalStat();
        Intent intent = new Intent(getContext(), TransactionActivity.class);
        intent.putExtra(TRANS_STAT, transStat);
        intent.putExtra(TRANS_TYP, planned);
        startActivity(intent);
    }

    private void setNormalStat(){
        fab_add.setImageResource(R.drawable.ic_add_white_24dp);
        fab_add.setVisibility(View.VISIBLE);
        fab_add_plus.setVisibility(View.INVISIBLE);
        fab_add_minus.setVisibility(View.INVISIBLE);
        fab_add_plus_planned.setVisibility(View.INVISIBLE);
        fab_add_minus_planned.setVisibility(View.INVISIBLE);
    }

    private void setSummen() {
        textView_spending_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_planned));
        textView_spending_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));

        textView_spending_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_fact));
        textView_spending_fact.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));

        textView_receipts_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_planned));
        textView_receipts_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

        textView_receipts_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_fact));
        textView_receipts_fact.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

        textView_total_fact.setText(String.format(Locale.getDefault(),"%1$,.2f", total_fact));
        textView_total_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", total_planned));

        if (total_planned > 0) {
            textView_total_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        } else {
            textView_total_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        }
        if (total_fact > 0) {
            textView_total_fact.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        } else {
            textView_total_fact.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        }
    }

    private void clearSummen() {
        amount_planned = 0;
        amount_fact = 0;
        total_planned = 0;
        total_fact = 0;
        receipts_fact = 0;
        receipts_planned = 0;
        spending_fact = 0;
        spending_planned = 0;
    }

    private void editTransaction(Transaction entry, String planned) {
        if(entry!=null) {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            intent.putExtra(TRANS_ID, entry.getId());
            intent.putExtra("click", 25);
            intent.putExtra(TRANS_TYP, planned);
            startActivity(intent);
        }
    }

    private void deleteTransaction(Transaction entry) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();
        databaseAdapter.deleteTransaction(entry.getId());
        databaseAdapter.close();
        getTransactions();
    }

    public void changeShowOnlyPlanned(boolean b) {
        this.showOnlyPlanned = b;
        getTransactions();
    }
}
