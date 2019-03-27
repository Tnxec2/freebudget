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
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.activity.MainActivity;
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

public class AllTransactionFragment extends Fragment {

    private static final String PREFS_KEY_LISTPOSITION = "LISTPOS";

    MainActivity main;

    private TextView textView_Month;
    private ListView listView_transactionsList;
    private Button btn_planRegular;
    private ImageButton btn_prevMonth, btn_nextMonth;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus, fab_add_plus_planned, fab_add_minus_planned;

    private List<Transaction> transactions = new ArrayList<>();

    TransactionAdapter transactionAdapter;

    private String[] months;

    boolean isMove;

    boolean showOnlyPlanned = false;

    public static long lastEditedId = 0;


    public AllTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_alltransaction, container, false);
        setHasOptionsMenu(true);
        return v;
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
        fab_add_plus_planned = (FloatingActionButton) view.findViewById(R.id.fab_add_plus_planned);
        fab_add_minus_planned = (FloatingActionButton) view.findViewById(R.id.fab_add_minus_planned);

        btn_planRegular = (Button) view.findViewById(R.id.btn_planRegular);

        btn_planRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planRegular();
            }
        });

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

        listView_transactionsList = (ListView) view.findViewById(R.id.listView_transactions);

        listView_transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction entry = transactionAdapter.getItem(position);
                editTransaction(entry, TRANS_TYP_FACT);
            }
        });

        // set list adapter
        transactionAdapter = new TransactionAdapter(getContext(), R.layout.list_view_item_transaction_item, transactions);
        // set adapter
        listView_transactionsList.setAdapter(transactionAdapter);
        // Register the ListView  for Context menu
        registerForContextMenu(listView_transactionsList);

        this.months = getResources().getStringArray(R.array.months);

        Calendar date = Calendar.getInstance();

        main = (MainActivity) getActivity();

        setMonthTextView();

        ConstraintLayout mainLayout = (ConstraintLayout) view.findViewById(R.id.mainlayout_alltransaction);
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

        listView_transactionsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fab_add.hide();
                        break;
                    case MotionEvent.ACTION_UP:
                        fab_add.show();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        fab_add.show();
                        break;
                }
                return false;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.all_transaction_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menuitem_load_regular :
                planRegular();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        main.setPosition(main.INDEX_DRAWER_ALLTRANSACTION);
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

        Transaction transaction = transactions.get(listPosition);
        switch (item.getItemId()) {
            case R.id.popup_edit:
                editTransaction(transaction, TRANS_TYP_FACT);
                break;
            case R.id.popup_edit_planned:
                editTransaction(transaction, TRANS_TYP_PLANNED);
                break;
            case R.id.popup_delete:
                deleteTransaction(transaction);
                break;
        }
        return true;
    }

    private void getTransactions() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        databaseAdapter.open();

        transactions.clear();
        //transactions.addAll(databaseAdapter.getTransactions(getContext(), this.year, this.month, showOnlyPlanned));

        lastEditedId = 0;
        lastEditedId = 0;
        long lastEditDate = 0;
        List<Transaction> dbTransactions = databaseAdapter.getTransactions(getContext(), main.year, main.month, false) ;
        for (Transaction transaction: dbTransactions) {
            if ( !showOnlyPlanned || transaction.getAmount_fact() == 0 ) {
                transactions.add(transaction);

                if ( transaction.getAmount_fact() != 0 && transaction.getDate_edit() > lastEditDate ) {
                    lastEditDate = transaction.getDate_edit();
                    lastEditedId = transaction.getId();
                }
            }
        }

        //
        if ( transactions.size() == 0) {
            btn_planRegular.setVisibility(View.VISIBLE);
        } else {
            btn_planRegular.setVisibility(View.GONE);
        }

        transactionAdapter.notifyDataSetChanged();

        databaseAdapter.close();
    }

    public void prevMonth(){
        main.prevMonth();
        setMonthTextView();
        this.getTransactions();
    }

    public void nextMonth(){
        main.nextMonth();
        setMonthTextView();
        this.getTransactions();
    }

    private void setMonthTextView() {
        this.textView_Month.setText(String.format(Locale.getDefault(),"%d / %s", main.year, this.months[main.month]));
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
        fab_add_plus_planned.setVisibility(View.INVISIBLE);
        fab_add_minus_planned.setVisibility(View.INVISIBLE);
    }

    public void changeShowOnlyPlanned(boolean b) {
        this.showOnlyPlanned = b;
        getTransactions();
    }

    private void editTransaction(Transaction entry, String planned) {
        if(entry!=null) {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            intent.putExtra(TRANS_ID, entry.getId());
            // intent.putExtra("click", 25);
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

    private void planRegular() {
        PlanRegular.setRegularToPlanned(getContext(), main.year, main.month);
        getTransactions();
    }
}
