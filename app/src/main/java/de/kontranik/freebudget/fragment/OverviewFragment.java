package de.kontranik.freebudget.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import de.kontranik.freebudget.adapter.CategoryAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.Transaction;
import de.kontranik.freebudget.service.OnSwipeTouchListener;

import static de.kontranik.freebudget.service.Constant.TRANS_STAT;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_MINUS;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_PLUS;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_FACT;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_PLANNED;

public class OverviewFragment extends Fragment implements View.OnClickListener {

    private ListView listView_categoryList;
    private TextView textView_Month;
    private TextView textView_receipts_planned, textView_spending_planned, textView_total_planned;
    private TextView textView_receipts_fact_planned, textView_receipts_fact_unplanned, textView_spending_fact_planned, textView_spending_fact_unplanned, textView_total_fact;

    private ImageButton btn_prevMonth, btn_nextMonth;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus, fab_add_plus_planned, fab_add_minus_planned;

    private int year, month;

    private String[] months;
    private List<Category> categoryList = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    public static double maxCategoryWeight = 0;
    public static int maxWidth;

    Boolean isMove;

    double amount_planned, amount_fact;
    double receipts_planned = 0;
    double receipts_fact_planned = 0;
    double receipts_fact_unplanned = 0;
    double spending_planned = 0;
    double spending_fact_planned = 0;
    double spending_fact_unplanned = 0;
    double total_planned = 0;
    double total_fact = 0;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        clearSummen();
        return inflater.inflate(R.layout.fragment_overview, parent, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxWidth = size.x;

        listView_categoryList = (ListView) view.findViewById(R.id.listView_categoryList);
        textView_Month = (TextView) view.findViewById(R.id.textView_Month);

        textView_receipts_planned = (TextView) view.findViewById(R.id.textView_receipts_planned);
        textView_receipts_fact_planned = (TextView) view.findViewById(R.id.textView_receipts_fact_planned);
        textView_receipts_fact_unplanned = (TextView) view.findViewById(R.id.textView_receipts_fact_unplanned);
        textView_spending_planned = (TextView) view.findViewById(R.id.textView_spending_planned);
        textView_spending_fact_planned = (TextView) view.findViewById(R.id.textView_spending_fact_planned);
        textView_spending_fact_unplanned = (TextView) view.findViewById(R.id.textView_spending_fact_unplanned);
        textView_total_planned = (TextView) view.findViewById(R.id.textView_total_planned);
        textView_total_fact = (TextView) view.findViewById(R.id.textView_total_fact);

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

        btn_prevMonth.setOnClickListener(this);
        btn_nextMonth.setOnClickListener(this);

        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.linearLayout_overview);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeLeft(){
                nextMonth();
            }
            public void onSwipeRight(){
                prevMonth();
            }
        });

        listView_categoryList.setOnDragListener(new View.OnDragListener() {
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

        categoryAdapter = new CategoryAdapter(view.getContext(), R.layout.list_view_item_categorygraph, categoryList);
        listView_categoryList.setAdapter(categoryAdapter);

        listView_categoryList.setOnTouchListener(new View.OnTouchListener() {
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
    public void onResume() {
        super.onResume();
        this.getTransactions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

    public void getTransactions () {

        DatabaseAdapter dbAdapter = new DatabaseAdapter(getContext());
        dbAdapter.open();

        /*
         * damit ListAdapter mitkriegt, dass die Liste geändert wurde,
         * muss diese Liste hier zuerst geputzt werden
         * und danach neue Liste ge-added werden
         */

        categoryList.clear();

        // als erstes komplett alle bewegungen für den Monat lesen
        List<Transaction> dbTransactions = dbAdapter.getTransactions(getContext(), this.year, this.month, false) ;

        clearSummen();

        for (Transaction transaction: dbTransactions) {
            amount_planned = transaction.getAmount_planned();
            if (amount_planned > 0) receipts_planned += amount_planned;
            else spending_planned += Math.abs(amount_planned);
            total_planned = total_planned + amount_planned;

            amount_fact = transaction.getAmount_fact();
            if ( amount_fact > 0 ) {
                if ( amount_planned > 0 ) receipts_fact_planned += amount_fact;
                else receipts_fact_unplanned += amount_fact;
            } else if ( amount_fact < 0 ) {
                if ( amount_planned < 0 ) spending_fact_planned += Math.abs(amount_fact);
                else spending_fact_unplanned += Math.abs(amount_fact);
            }
            total_fact += amount_fact;

            if ( transaction.getCategory().trim().length() > 0 ) {
                if ( amount_fact < 0 ) {
                    int ix = 0;
                    for (Category category : categoryList) {
                        if ( transaction.getCategory().equals(category.getName()) ) {
                            category.setWeight(category.getWeight() + Math.abs(amount_fact));
                            if ( category.getWeight() > maxCategoryWeight ) maxCategoryWeight = category.getWeight();
                        }
                    }
                    if (ix == 0) {
                        Category newCat = new Category(0, transaction.getCategory(), Math.abs(amount_fact));
                        categoryList.add(newCat);
                        if ( newCat.getWeight() > maxCategoryWeight ) maxCategoryWeight = newCat.getWeight();
                    }
                }
            }
        }

        setSummen();

        dbAdapter.close();

        // und den Adapter aktualisieren
        categoryAdapter.notifyDataSetChanged();
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

        textView_spending_fact_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_fact_planned));
        textView_spending_fact_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));

        textView_spending_fact_unplanned.setText(String.format(Locale.getDefault(),"%1$,.2f", spending_fact_unplanned));
        textView_spending_fact_unplanned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));

        textView_receipts_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_planned));
        textView_receipts_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

        textView_receipts_fact_planned.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_fact_planned));
        textView_receipts_fact_planned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

        textView_receipts_fact_unplanned.setText(String.format(Locale.getDefault(),"%1$,.2f", receipts_fact_unplanned));
        textView_receipts_fact_unplanned.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

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
        receipts_fact_planned = 0;
        receipts_fact_unplanned = 0;
        receipts_planned = 0;
        spending_fact_planned = 0;
        spending_fact_unplanned = 0;
        spending_planned = 0;
    }
}
