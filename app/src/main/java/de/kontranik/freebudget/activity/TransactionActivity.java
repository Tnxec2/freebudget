package de.kontranik.freebudget.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.RadioButton;

import java.util.Calendar;
import java.util.List;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.activity.CategoryListActivity.RESULT_CATEGORY;

public class TransactionActivity extends AppCompatActivity {

    static final int PICK_CATEGORY_REQUEST = 123;  // The request code

    private EditText descriptionBox;
    private AutoCompleteTextView categoryBox;
    private EditText amountBox;
    private EditText dateBox;
    private Button delButton, copyButton;
    private DatabaseAdapter dbAdapter;
    private long transactionID = 0;
    private RadioButton radioButtonReceipts, radioButtonSpending;

    DatePickerDialog datePickerDialog;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_activity_transaction);
        setContentView(R.layout.activity_transaction);

        descriptionBox = (EditText) findViewById(R.id.editText_description);
        categoryBox = findViewById(R.id.acTextView_category);
        amountBox = (EditText) findViewById(R.id.editText_amount);
        delButton = (Button) findViewById(R.id.button_delete);
        copyButton = (Button) findViewById(R.id.button_copy);

        // initiate the date picker and a button
        dateBox = (EditText) findViewById(R.id.editText_date);

        dbAdapter = new DatabaseAdapter(this);

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

        radioButtonReceipts = (RadioButton) findViewById(R.id.radioButton_receipts);
        radioButtonSpending = (RadioButton) findViewById(R.id.radioButton_spending);

        // perform click event on edit text
        dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR); // current year
                month = c.get(Calendar.MONTH); // current month
                day = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(TransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dateBox.setText(new StringBuilder().append(day).append("/")
                                        .append(month).append("/").append(year));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionID = extras.getLong("id");
        }
        // if ID = 0, then create new transaction; else update
        if (transactionID > 0) {
            // получаем элемент по id из бд
            dbAdapter.open();
            Transaction transaction = dbAdapter.getTransaction(transactionID);
            descriptionBox.setText(transaction.getDescription());

            Category category = dbAdapter.getCategory(transaction.getCategory());
            categoryBox.setText(category.getName());

            if (transaction.getAmount_fact() != 0) {
                amountBox.setText(String.valueOf(Math.abs(transaction.getAmount_fact())));
            }
            if (transaction.getAmount_fact() > 0) {
                radioButtonReceipts.setChecked(true);
            } else if (transaction.getAmount_fact() < 0 ) {
                radioButtonSpending.setChecked(true);
            }

            dbAdapter.close();
        } else {
            String transStat = "";
            if (extras != null) {
                transStat = extras.getString("TRANS_STAT");
            }

            assert transStat != null;
            if (transStat.equals("plus")) {
                radioButtonReceipts.setChecked(true);
            } else {
                radioButtonSpending.setChecked(true);
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

    // find Index of Spinner by Value
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void saveAndClose(View view) {
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

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        dbAdapter.open();

        Transaction entry =
                new Transaction(
                        transactionID, (long) 0, description, categoryName, cal.getTimeInMillis(), cal.getTimeInMillis(), amount, amount);

        if (transactionID > 0) {
            dbAdapter.update(entry);
        } else {
            dbAdapter.insert(entry);
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
        dbAdapter.deleteTransaction(transactionID);
        dbAdapter.close();
        this.finish();
        //goHome();
    }

    private void goHome(){
        // zu Main-Activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void selectCat(View view) {
        Intent intent = new Intent(this, CategoryListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, PICK_CATEGORY_REQUEST );
    }
}