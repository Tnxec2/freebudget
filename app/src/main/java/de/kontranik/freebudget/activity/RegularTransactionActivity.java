package de.kontranik.freebudget.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.RegularTransaction;

import static de.kontranik.freebudget.activity.CategoryListActivity.RESULT_CATEGORY;

public class RegularTransactionActivity extends AppCompatActivity {

    public static final String TRANS_STAT = "TRANS_STAT";
    public static final String MONTH = "MONTH";

    static final int PICK_CATEGORY_REQUEST = 123;  // The request code


    private EditText descriptionBox;
    private AutoCompleteTextView categoryBox;
    private EditText amountBox;
    private EditText dayBox;
    private Spinner monthBox;
    private Button delButton, copyButton, selectCatButton;
    private RadioButton radioButtonReceipts, radioButtonSpending;

    private DatabaseAdapter dbAdapter;
    private long transactionID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_transaction);

        setTitle(R.string.new_regular_transaction);

        descriptionBox = (EditText) findViewById(R.id.editText_description_regular);
        categoryBox = (AutoCompleteTextView) findViewById(R.id.acTextView_category_regular);

        amountBox = (EditText) findViewById(R.id.editText_amount_regular);
        dayBox = (EditText) findViewById(R.id.editText_day_regular) ;
        monthBox = (Spinner) findViewById(R.id.spinner_Month_regular);
        delButton = (Button) findViewById(R.id.button_delete_regular);
        copyButton = (Button) findViewById(R.id.button_copy_regular);
        selectCatButton = findViewById(R.id.btn_selectCat);

        dbAdapter = new DatabaseAdapter(this);

        radioButtonReceipts = (RadioButton) findViewById(R.id.radioButton_receipts_regular);
        radioButtonSpending = (RadioButton) findViewById(R.id.radioButton_spending_regular);

        dbAdapter.open();
        List<Category> categoryArrayList = dbAdapter.getAllCategory();
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
                this, android.R.layout.simple_dropdown_item_1line, categoryArrayList);
        categoryBox.setAdapter(adapter);
        categoryBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Category selected = (Category) arg0.getAdapter().getItem(arg2);
                /*
                Toast.makeText(RegularTransactionActivity.this,
                        "Clicked " + arg2 + " name: " + selected.getName(),
                        Toast.LENGTH_SHORT).show();
                */
            }
        });

        // get ID from Buffer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionID = extras.getLong("id");
        }
        // if ID = 0, then create new transaction; else update
        monthBox.setSelection(0);
        if (transactionID > 0) {
            // получаем элемент по id из бд
            dbAdapter.open();
            RegularTransaction transaction = dbAdapter.getRegularTransaction(transactionID);
            if ( transaction != null) {
                Category category = dbAdapter.getCategory(transaction.getCategory());
                descriptionBox.setText(transaction.getDescription());
                categoryBox.setText(category.getName());
                if (transaction.getAmount() != 0) {
                    amountBox.setText(String.valueOf(Math.abs(transaction.getAmount())));
                }
                if (transaction.getAmount() > 0) {
                    radioButtonReceipts.setChecked(true);
                } else if (transaction.getAmount() < 0) {
                    radioButtonSpending.setChecked(true);
                }
                monthBox.setSelection(transaction.getMonth());
            }
            dbAdapter.close();
        } else {
            String transStat = "";
            if (extras != null) {
                if ( extras.containsKey(TRANS_STAT))
                    transStat = extras.getString(TRANS_STAT);
                if ( extras.containsKey(MONTH))
                    monthBox.setSelection(extras.getInt(MONTH));
            }

            if ( transStat != null ) {
                if (transStat.equals("plus")) {
                    radioButtonReceipts.setChecked(true);
                } else {
                    radioButtonSpending.setChecked(true);
                }
            }

            // hide delete button
            delButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                categoryBox.setText( data.getStringExtra(RESULT_CATEGORY) );
            }
        }
    }

    public void saveAndClose (View view) {
        save(view);
        this.finish();
        //goHome();
    }

    public void save(View view){

        String description = descriptionBox.getText().toString();
        String categoryName = categoryBox.getText().toString();
        double amount;
        try {
            amount = Double.parseDouble(amountBox.getText().toString());
            if(radioButtonReceipts.isChecked()) {
                amount = Math.abs(amount);
            } else if(radioButtonSpending.isChecked()) {
                amount = 0 - Math.abs(amount);
            }
        } catch (Exception e) {
            amount = 0;
        }

        int month = monthBox.getSelectedItemPosition();
        int day = Integer.parseInt(dayBox.getText().toString());

        dbAdapter.open();

        RegularTransaction regularTransaction =
                new RegularTransaction(transactionID, month, day, description, categoryName, amount);

        if (transactionID > 0) {
            dbAdapter.update(regularTransaction);
        } else {
            dbAdapter.insert(regularTransaction);
        }
        dbAdapter.close();
    }

    public void copy(View view){
        transactionID = 0;
        delButton.setVisibility(View.GONE);
        copyButton.setVisibility(View.GONE);
    }

    public void delete(View view){
        dbAdapter.open();
        dbAdapter.deleteRegularTransaction(transactionID);
        dbAdapter.close();
        this.finish();
        //goHome();
    }

    private void goHome(){
        // zu Main-Activity
        Intent intent = new Intent(this, ManageRegularTransactionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void selectCat(View view) {
        Intent intent = new Intent(this, CategoryListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, PICK_CATEGORY_REQUEST );
    }
}
