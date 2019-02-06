package de.kontranik.freebudget.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.RadioButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.kontranik.freebudget.R;

import de.kontranik.freebudget.service.SoftKeyboard;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.Transaction;

import static de.kontranik.freebudget.activity.CategoryListActivity.RESULT_CATEGORY;
import static de.kontranik.freebudget.service.Constant.TRANS_ID;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_MINUS;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_PLUS;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP;
import static de.kontranik.freebudget.service.Constant.TRANS_TYP_PLANNED;

public class TransactionActivity extends AppCompatActivity {

    static final int PICK_CATEGORY_REQUEST = 123;  // The request code

    private EditText editTextDescription;
    private AutoCompleteTextView acTextViewCategory;
    private EditText editTextAmountPlanned, editTextAmountFact;
    private EditText editTextDate;
    private Button buttonDelete, buttonCopy;
    private ImageButton buttonCopyAmount;
    private RadioButton radioButtonReceipts, radioButtonSpending;

    private DatabaseAdapter dbAdapter;
    private long transactionID = 0;

    DatePickerDialog datePickerDialog;
    public int year, month, day;
    public boolean planned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_activity_transaction);
        setContentView(R.layout.activity_transaction);

        editTextDescription = (EditText) findViewById(R.id.editText_description);

        editTextDescription.requestFocus();
        SoftKeyboard.showKeyboard(this);

        acTextViewCategory = (AutoCompleteTextView) findViewById(R.id.acTextView_category);
        editTextAmountPlanned = (EditText) findViewById(R.id.editText_amount_planned);
        editTextAmountFact = (EditText) findViewById(R.id.editText_amount_fact);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonCopy = (Button) findViewById(R.id.button_copy);
        buttonCopyAmount = (ImageButton) findViewById(R.id.btn_copy_amount);

        // initiate the date picker and a button
        editTextDate = (EditText) findViewById(R.id.editText_date);

        dbAdapter = new DatabaseAdapter(this);

        dbAdapter.open();
        List<Category> categoryArrayList = dbAdapter.getAllCategory();
        dbAdapter.close();

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, categoryArrayList);
        acTextViewCategory.setAdapter(adapter);
        acTextViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // Category selected = (Category) arg0.getAdapter().getItem(arg2);
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
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                if ( day == 0 ) {
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR); // current year
                    month = c.get(Calendar.MONTH) + 1; // current month
                    day = c.get(Calendar.DAY_OF_MONTH); // current day
                }
                // date picker dialog
                datePickerDialog = new DatePickerDialog(TransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                setDateBox(year, monthOfYear+1, dayOfMonth);
                            }
                        }, year, month - 1, day);
                datePickerDialog.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if ( extras.containsKey(TRANS_ID) ) transactionID = extras.getLong(TRANS_ID);
            if ( extras.containsKey(TRANS_TYP) ) planned = extras.getString(TRANS_TYP).equals(TRANS_TYP_PLANNED);
        }
        //
        long displayDate;

        if (transactionID > 0) {
            // get entry from db
            dbAdapter.open();
            Transaction transaction = dbAdapter.getTransaction(transactionID);
            editTextDescription.setText(transaction.getDescription());

            acTextViewCategory.setText(transaction.getCategory());

            editTextAmountFact.setText(String.valueOf(Math.abs(transaction.getAmount_fact())));
            editTextAmountPlanned.setText(String.valueOf(Math.abs(transaction.getAmount_planned())));

            if (transaction.getDate() > 0) displayDate = transaction.getDate();
            else displayDate = new Date().getTime();

            if (transaction.getAmount_fact() > 0) {
                radioButtonReceipts.setChecked(true);
            } else if (transaction.getAmount_fact() < 0 ) {
                radioButtonSpending.setChecked(true);
            } else if (transaction.getAmount_planned() > 0) {
                radioButtonReceipts.setChecked(true);
            } else if (transaction.getAmount_planned() < 0 ) {
                radioButtonSpending.setChecked(true);
            }

            dbAdapter.close();
        } else {
            String transStat = TRANS_STAT_MINUS;

            if (extras != null) {
                if ( extras.containsKey(TRANS_STAT) ) transStat = extras.getString(TRANS_STAT);
            }

            if (transStat != null && transStat.equals(TRANS_STAT_PLUS)) {
                radioButtonReceipts.setChecked(true);
            } else {
                radioButtonSpending.setChecked(true);
            }

            displayDate = new Date().getTime();

            // hide delete button
            buttonDelete.setVisibility(View.GONE);

            Log.d("NIK", String.valueOf(planned));

        }

        if ( displayDate > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(displayDate));
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH) + 1;
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        setDateBox(year, month, day);

        editTextAmountPlanned.setEnabled(planned);
        editTextAmountFact.setEnabled(!planned);
        buttonCopyAmount.setEnabled(!planned);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                acTextViewCategory.setText( data.getStringExtra(RESULT_CATEGORY) );
            }
        }
    }

    private void setDateBox(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        editTextDate.setText( String.format("%02d/%02d/%04d", day, month, year) );
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
        SoftKeyboard.hideKeyboard(this);
        this.finish();
        //goHome();
    }

    public void save(View view){

        String description = editTextDescription.getText().toString();
        String categoryName = acTextViewCategory.getText().toString();
        double amountPlanned, amountFact;
        try {
            amountPlanned = Double.parseDouble(editTextAmountPlanned.getText().toString());
            if(radioButtonReceipts.isChecked()) {
                amountPlanned = Math.abs(amountPlanned);
            } else if(radioButtonSpending.isChecked()) {
                amountPlanned = 0 - Math.abs(amountPlanned);
            }
        } catch (Exception e) {
            amountPlanned = 0;
        }

        try {
            amountFact = Double.parseDouble(editTextAmountFact.getText().toString());
            if(radioButtonReceipts.isChecked()) {
                amountFact = Math.abs(amountFact);
            } else if(radioButtonSpending.isChecked()) {
                amountFact = 0 - Math.abs(amountFact);
            }
        } catch (Exception e) {
            amountFact = 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        dbAdapter.open();

        Transaction entry = new Transaction(
                transactionID, (long) 0, description, categoryName, cal.getTimeInMillis(), amountPlanned, amountFact);

        if (planned) {
            entry.setAmount_planned( amountPlanned );
        } else {
            entry.setAmount_fact( amountFact );
            if ( transactionID > 0) {
                Transaction dbentry = dbAdapter.getTransaction(transactionID);
                if (dbentry != null) {
                    entry.setRegular_id(dbentry.getRegular_id());
                    entry.setAmount_planned(dbentry.getAmount_planned());
                }
            }
        }

        if (transactionID > 0) {
            dbAdapter.update(entry);
        } else {
            dbAdapter.insert(entry);
        }
        dbAdapter.close();

    }

    public void copy(View view){
        transactionID = 0;
        buttonDelete.setVisibility(View.GONE);
        buttonCopy.setVisibility(View.GONE);
    }

    public void delete(View view){
        dbAdapter.open();
        dbAdapter.deleteTransaction(transactionID);
        dbAdapter.close();
        SoftKeyboard.hideKeyboard(this);
        this.finish();
        //goHome();
    }

    public void selectCat(View view) {
        Intent intent = new Intent(this, CategoryListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, PICK_CATEGORY_REQUEST );
    }

    public void copyAmount(View view) {
        editTextAmountFact.setText( editTextAmountPlanned.getText() );
    }
}