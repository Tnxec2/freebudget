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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.service.SoftKeyboard;
import de.kontranik.freebudget.database.DatabaseAdapter;
import de.kontranik.freebudget.model.Category;
import de.kontranik.freebudget.model.RegularTransaction;

import static de.kontranik.freebudget.activity.CategoryListActivity.RESULT_CATEGORY;
import static de.kontranik.freebudget.service.Constant.TRANS_ID;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT;
import static de.kontranik.freebudget.service.Constant.TRANS_STAT_PLUS;

public class RegularTransactionActivity extends AppCompatActivity {

    public static final String MONTH = "MONTH";

    static final int PICK_CATEGORY_REQUEST = 123;  // The request code
    static final String REGULAR_DATE_START = "date_start";
    static final String REGULAR_DATE_END = "date_end";

    private EditText editTextDescription;
    private AutoCompleteTextView acTextViewCategory;
    private EditText editTextAmount;
    private EditText editTextDay;
    private Spinner spinnerMonth;
    private Button button_start_date, button_end_date;
    private Button buttonDelete, buttonCopy;
    private RadioButton radioButtonReceipts, radioButtonSpending;

    private DatabaseAdapter dbAdapter;

    private long transactionID = 0;

    private long date_start, date_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_transaction);

        setTitle(R.string.new_regular_transaction);

        editTextDescription = (EditText) findViewById(R.id.editText_description_regular);

        editTextDescription.requestFocus();
        SoftKeyboard.showKeyboard(this  );

        acTextViewCategory = (AutoCompleteTextView) findViewById(R.id.acTextView_category_regular);

        editTextAmount = (EditText) findViewById(R.id.editText_amount_regular);
        editTextDay = (EditText) findViewById(R.id.editText_day_regular);
        spinnerMonth = (Spinner) findViewById(R.id.spinner_Month_regular);
        buttonDelete = (Button) findViewById(R.id.button_delete_regular);
        buttonCopy = (Button) findViewById(R.id.button_copy_regular);

        button_start_date = (Button) findViewById(R.id.button_start_date);
        button_end_date = (Button) findViewById(R.id.button_end_date);

        ImageButton imageButton_clear_start_date = (ImageButton) findViewById(R.id.imageButton_clear_start_date);
        ImageButton imageButton_clear_end_date = (ImageButton) findViewById(R.id.imageButton_clear_end_date);

        dbAdapter = new DatabaseAdapter(this);

        radioButtonReceipts = (RadioButton) findViewById(R.id.radioButton_receipts_regular);
        radioButtonSpending = (RadioButton) findViewById(R.id.radioButton_spending_regular);

        // perform click event on edit text
        button_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar newCalendar = Calendar.getInstance();
                if (date_start == 0) {
                    date_start = newCalendar.getTimeInMillis();
                } else {
                    newCalendar.setTimeInMillis(date_start);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegularTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar datePickerDate = Calendar.getInstance();
                        datePickerDate.clear();
                        datePickerDate.set(year, monthOfYear, dayOfMonth, 0, 0, 1);
                        datePickerDate.set(Calendar.MILLISECOND, 0);
                        date_start = datePickerDate.getTimeInMillis();

                        setDateText(button_start_date, date_start);
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        // perform click event on edit text
        button_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar newCalendar = Calendar.getInstance();
                if (date_end == 0) {
                    date_end = newCalendar.getTimeInMillis();
                } else {
                    newCalendar.setTimeInMillis(date_end);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegularTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar datePickerDate = Calendar.getInstance();
                        datePickerDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                        datePickerDate.set(Calendar.MILLISECOND, 999);
                        date_end = datePickerDate.getTimeInMillis();
                        setDateText(button_end_date, date_end);
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        imageButton_clear_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_start = 0;
                button_start_date.setText(R.string.not_set);
            }
        });

        imageButton_clear_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_end = 0;
                button_end_date.setText(R.string.not_set);
            }
        });

        dbAdapter.open();
        List<Category> categoryArrayList = dbAdapter.getAllCategory();
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

        // get ID from Buffer
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            transactionID = extras.getLong(TRANS_ID);
        }
        // if ID = 0, then create new transaction; else update
        spinnerMonth.setSelection(0);
        if (transactionID > 0) {
            // получаем элемент по id из бд
            dbAdapter.open();
            RegularTransaction transaction = dbAdapter.getRegularById(transactionID);
            if ( transaction != null) {
                editTextDescription.setText(transaction.getDescription());
                acTextViewCategory.setText(transaction.getCategory());
                editTextDay.setText(String.valueOf(transaction.getDay()));

                if (transaction.getAmount() != 0) {
                    editTextAmount.setText(String.valueOf(Math.abs(transaction.getAmount())));
                }
                if (transaction.getAmount() > 0) {
                    radioButtonReceipts.setChecked(true);
                } else if (transaction.getAmount() < 0) {
                    radioButtonSpending.setChecked(true);
                }
                spinnerMonth.setSelection(transaction.getMonth());
                date_start = transaction.getDate_start();
                date_end = transaction.getDate_end();
            }
            dbAdapter.close();
        } else {
            String transStat = null;
            if (extras != null) {
                if ( extras.containsKey(TRANS_STAT))
                    transStat = extras.getString(TRANS_STAT);
                if ( extras.containsKey(MONTH))
                    spinnerMonth.setSelection(extras.getInt(MONTH));
            }

            if ( transStat != null ) {
                if (transStat.equals(TRANS_STAT_PLUS)) {
                    radioButtonReceipts.setChecked(true);
                } else {
                    radioButtonSpending.setChecked(true);
                }
            }

            // hide delete button
            buttonDelete.setVisibility(View.GONE);
            date_start = 0;
            date_end = 0;
        }

        setDateText(button_start_date, date_start);
        setDateText(button_end_date, date_end);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(REGULAR_DATE_START, this.date_start);
        outState.putLong(REGULAR_DATE_END, this.date_end);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if ( savedInstanceState.containsKey(REGULAR_DATE_START) ) {
                date_start = savedInstanceState.getLong(REGULAR_DATE_START);
                setDateText(button_start_date, date_start);
            }
            if ( savedInstanceState.containsKey(REGULAR_DATE_END) ) {
                date_end = savedInstanceState.getLong(REGULAR_DATE_END);
                setDateText(button_end_date, date_end);
            }
        }
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

    public void saveAndClose (View view) {
        save(view);
        SoftKeyboard.hideKeyboard(this);
        this.finish();
        //goHome();
    }

    public void save(View view){

        String description = editTextDescription.getText().toString();
        String categoryName = acTextViewCategory.getText().toString();
        double amount;
        try {
            amount = Double.parseDouble(editTextAmount.getText().toString());
            if(radioButtonReceipts.isChecked()) {
                amount = Math.abs(amount);
            } else if(radioButtonSpending.isChecked()) {
                amount = 0 - Math.abs(amount);
            }
        } catch (Exception e) {
            amount = 0;
        }

        int month = spinnerMonth.getSelectedItemPosition();
        int day = Integer.parseInt(editTextDay.getText().toString());

        dbAdapter.open();

        RegularTransaction regularTransaction =
                new RegularTransaction(transactionID, month, day, description, categoryName, amount);
        regularTransaction.setDate_start(date_start);
        regularTransaction.setDate_end(date_end);

        if (transactionID > 0) {
            dbAdapter.update(regularTransaction);
        } else {
            dbAdapter.insert(regularTransaction);
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
        dbAdapter.deleteRegularTransaction(transactionID);
        dbAdapter.close();
        SoftKeyboard.hideKeyboard(this);
        this.finish();
    }

    public void selectCat(View view) {
        Intent intent = new Intent(this, CategoryListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, PICK_CATEGORY_REQUEST );
    }

    private void setDateText( Button button, long date) {
        if ( date == 0 ) {
            button.setText(R.string.not_set);
        } else {
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            button.setText(dateFormatter.format(date));
        }
    }
}
