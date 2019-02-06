package de.kontranik.freebudget.fragment;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import de.kontranik.freebudget.activity.RegularTransactionActivity;
import de.kontranik.freebudget.adapter.RegularTransactionAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.RegularTransaction;
import de.kontranik.freebudget.service.OnSwipeTouchListener;

import static de.kontranik.freebudget.activity.RegularTransactionActivity.MONTH;
import static de.kontranik.freebudget.activity.RegularTransactionActivity.TRANS_STAT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegularFragment} interface
 * to handle interaction events.
 * Use the {@link RegularFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegularFragment extends Fragment {

    static final int RESULT_OPEN_FILENAME = 234;

    private ListView listView_Transactions;
    private TextView textView_Month;
    private TextView textView_receipts, textView_spending, textView_total;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;
    private ImageButton btn_prevMonth, btn_nextMonth;

    private int month;
    private String[] months;
    static String transStat;
    Boolean isMove;

    List<RegularTransaction> transactionList = new ArrayList<>();
    RegularTransactionAdapter transactionAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegularFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegularFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegularFragment newInstance(String param1, String param2) {
        RegularFragment fragment = new RegularFragment();
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
        return inflater.inflate(R.layout.fragment_regular, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        getActivity().setTitle(R.string.manage_regular);

        listView_Transactions = (ListView) view.findViewById(R.id.listView_regular_transactions);
        textView_Month = (TextView) view.findViewById(R.id.textView_Month_Regular);

        textView_receipts = (TextView) view.findViewById(R.id.textView_receipts_regular);
        textView_spending = (TextView) view.findViewById(R.id.textView_spending_regular);
        textView_total = (TextView) view.findViewById(R.id.textView_total_regular);

        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add_regular);
        fab_add_plus = (FloatingActionButton) view.findViewById(R.id.fab_add_plus_regular);
        fab_add_minus = (FloatingActionButton) view.findViewById(R.id.fab_add_minus_regular);

        btn_prevMonth = (ImageButton) view.findViewById(R.id.btn_prevMonth);
        btn_nextMonth = (ImageButton) view.findViewById(R.id.btn_nextMonth);

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

        ConstraintLayout mainLayout = (ConstraintLayout) view.findViewById(R.id.mainlayout_regular);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();
        month = date.get(Calendar.MONTH)+1;
        textView_Month.setText(this.months[month]);

        listView_Transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegularTransaction entry = transactionAdapter.getItem(position);
                if(entry!=null) {
                    Intent intent = new Intent(getContext(), RegularTransactionActivity.class);
                    intent.putExtra("id", entry.getId());
                    intent.putExtra("click", 25);
                    startActivity(intent);
                }
            }
        });

        // set list adapter
        transactionAdapter = new RegularTransactionAdapter(getContext(),
                R.layout.layout_regular_transaction_item,
                transactionList);
        // set adapter
        listView_Transactions.setAdapter(transactionAdapter);



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
    }

    @Override
    public void onResume() {
        super.onResume();
        getTransactions();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

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

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
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
        textView_spending.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        textView_receipts.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts));
        textView_receipts.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        textView_total.setText(String.format(Locale.getDefault(),"%1$,.2f", total));
        if (total > 0) {
            textView_total.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        } else {
            textView_total.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        }

        databaseAdapter.close();

        transactionAdapter.notifyDataSetChanged();
    }

    public void add(View view){
        Intent intent = new Intent(getContext(), RegularTransactionActivity.class);
        startActivity(intent);
    }

    public void add(){
        setNormalStat();
        Intent intent = new Intent(getContext(), RegularTransactionActivity.class);
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
