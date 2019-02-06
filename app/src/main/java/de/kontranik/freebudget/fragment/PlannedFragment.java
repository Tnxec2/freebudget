package de.kontranik.freebudget.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_FACT;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_PLANNED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlannedFragment} interface
 * to handle interaction events.
 * Use the {@link PlannedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlannedFragment extends Fragment {

    private TextView textView_Month;
    private ListView listView_transactionsList;
    private ImageButton btn_prevMonth, btn_nextMonth;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;

    private List<Transaction> transactions = new ArrayList<>();

    TransactionAdapter transactionAdapter;

    private int year, month;

    private String[] months;

    boolean isMove;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlannedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlannedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlannedFragment newInstance(String param1, String param2) {
        PlannedFragment fragment = new PlannedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                prevMonth(v);
            }
        });

        btn_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth(v);
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

    private void getTransactions() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();

        transactions.clear();
        transactions.addAll(databaseAdapter.getTransactions(this.year, this.month, true));
        transactionAdapter.notifyDataSetChanged();

        databaseAdapter.close();
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
