package de.kontranik.freebudget.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Transaction;

public class AllTransactionsActivity extends AppCompatActivity {

    private ListView listView_transactionsList;
    private FloatingActionButton fab_add, fab_add_plus, fab_add_minus;
    TransactionAdapter transactionAdapter;
    static String transStat;
    Boolean isMove;

     @SuppressLint("ClickableViewAccessibility")
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_all_transactions);
        setContentView(R.layout.activity_all_transactions);

        listView_transactionsList = (ListView)findViewById(R.id.listView_transactionsList);
        fab_add = (FloatingActionButton)findViewById(R.id.fab_add);
        fab_add_plus = (FloatingActionButton)findViewById(R.id.fab_add_plus);
        fab_add_minus = (FloatingActionButton)findViewById(R.id.fab_add_minus);

        listView_transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

         listView_transactionsList.setOnDragListener(new View.OnDragListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.alltransactions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.action_config :
                intent = new Intent(this, ConfigActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        List<Transaction> transactions = adapter.getTransactions(this);

        // set list adapter
        transactionAdapter = new TransactionAdapter(this,
                R.layout.layout_transaction_item,
                transactions);
        // set adapter
        listView_transactionsList.setAdapter(transactionAdapter);

        adapter.close();
    }

    public void add(View view){
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    public void add(){
        setNormalStat();
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra("TRANS_STAT", transStat);
        startActivity(intent);
    }

    private void setNormalStat(){
        fab_add.setImageResource(R.drawable.ic_add_white_24dp);
        fab_add.setVisibility(View.VISIBLE);
        fab_add_plus.setVisibility(View.INVISIBLE);
        fab_add_minus.setVisibility(View.INVISIBLE);
    }
}

