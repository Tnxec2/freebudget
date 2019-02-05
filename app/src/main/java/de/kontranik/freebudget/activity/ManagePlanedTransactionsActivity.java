package de.kontranik.freebudget.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.kontranik.freebudget.service.FileService;
import de.kontranik.freebudget.R;
import de.kontranik.freebudget.adapter.TransactionAdapter;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.activity.OpenFileActivity.RESULT_FILENAME;

public class ManagePlanedTransactionsActivity extends AppCompatActivity {

    static final int RESULT_OPEN_FILENAME = 345;

    public static final String TRANS_STAT = "TRANS_STAT";

    private ListView listView_transactionsList;

    private List<Transaction> transactions = new ArrayList<>();

    TransactionAdapter transactionAdapter;

     @SuppressLint("ClickableViewAccessibility")
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_all_transactions);
        setContentView(R.layout.activity_manage_planed_transactions);

        listView_transactionsList = findViewById(R.id.listView_transactionsList);

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

         // set list adapter
         transactionAdapter = new TransactionAdapter(this,
                 R.layout.layout_transaction_item,
                 transactions);
         // set adapter
         listView_transactionsList.setAdapter(transactionAdapter);
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
            case R.id.action_import_csv :
                Intent open_import = new Intent(this, OpenFileActivity.class);
                this.startActivityForResult(open_import, RESULT_OPEN_FILENAME);
                return true;
            case R.id.action_export_csv :
                try {
                    FileService.exportFileTransaction("export_freebudget_transaction", this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_OPEN_FILENAME:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);
                    Toast.makeText(this,
                            this.getResources().getString(R.string.importFromFile, fileName),
                            Toast.LENGTH_SHORT).show();
                    try {
                        FileService.importFileTransaction(fileName, this);
                    } catch (Exception e) {
                        Toast.makeText(this, this.getResources().getString(R.string.importFail, e.getLocalizedMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();

        transactions.clear();
        transactions.addAll(databaseAdapter.getAllPlanedTransactions(this));
        transactionAdapter.notifyDataSetChanged();

        databaseAdapter.close();
    }


}

