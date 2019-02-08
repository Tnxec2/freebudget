package de.kontranik.freebudget.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
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

import static de.kontranik.freebudget.service.Constant.TRANS_ID;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_MINUS;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_PLUS;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_PLANNED;

public class PlannedFragment extends Fragment {

    private static final String PREFS_KEY_LISTPOSITION = "LISTPOS";

    private TextView textView_Month;
    private ListView listView_transactionsList;
    private ImageButton btn_prevMonth, btn_nextMonth;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;

    private List<Transaction> transactions = new ArrayList<>();

    TransactionAdapter transactionAdapter;

    private int year, month;

    private String[] months;

    boolean isMove;

    public PlannedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planned, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        textView_Month = (TextView) view.findViewById(R.id.textView_Month);

        btn_prevMonth = (ImageButton) view.findViewById(R.id.btn_prevMonth);
        btn_nextMonth = (ImageButton) view.findViewById(R.id.btn_nextMonth);

        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab_add_plus = (FloatingActionButton) view.findViewById(R.id.fab_add_plus);
        fab_add_minus = (FloatingActionButton) view.findViewById(R.id.fab_add_minus);

        btn_prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth();
            }
        });

        btn_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        listView_transactionsList = (ListView) view.findViewById(R.id.listView_transactionsList);

        listView_transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction entry = transactionAdapter.getItem(position);
                if(entry != null) {
                    Intent intent = new Intent(getContext(), TransactionActivity.class);
                    intent.putExtra(TRANS_ID, entry.getId());
                    startActivity(intent);
                }
            }
        });

        // set list adapter
        transactionAdapter = new TransactionAdapter(getContext(), R.layout.layout_transaction_item, transactions);
        // set adapter
        listView_transactionsList.setAdapter(transactionAdapter);

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH)+1;


        ConstraintLayout mainLayout = (ConstraintLayout) view.findViewById(R.id.mainlayout_planned);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

        listView_transactionsList.setOnDragListener(new View.OnDragListener() {
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
                        add(TRANS_STAT_MINUS, TRANS_TYP_PLANNED);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fab_add_plus.setVisibility(View.VISIBLE);
                    fab_add_minus.setVisibility(View.VISIBLE);
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

        setMonthTextView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTransactions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState!=null) {
            outState.putInt(PREFS_KEY_LISTPOSITION, listView_transactionsList.getFirstVisiblePosition());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState!=null) {
            int listpos = savedInstanceState.getInt(PREFS_KEY_LISTPOSITION);
            listView_transactionsList.setSelection(listpos);
        }
    }

    private void getTransactions() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();

        transactions.clear();
        transactions.addAll(databaseAdapter.getTransactions(getContext(), this.year, this.month, true));
        transactionAdapter.notifyDataSetChanged();

        databaseAdapter.close();
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

    public void add(String transStat, String planned){
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
    }
}
