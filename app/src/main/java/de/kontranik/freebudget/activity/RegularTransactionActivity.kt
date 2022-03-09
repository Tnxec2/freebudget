package de.kontranik.freebudget.activity

import de.kontranik.freebudget.model.Category.name
import de.kontranik.freebudget.database.DatabaseAdapter.open
import de.kontranik.freebudget.database.DatabaseAdapter.getCategory
import de.kontranik.freebudget.database.DatabaseAdapter.close
import de.kontranik.freebudget.database.DatabaseAdapter.allCategory
import de.kontranik.freebudget.model.Category.id
import de.kontranik.freebudget.database.DatabaseAdapter.update
import de.kontranik.freebudget.database.DatabaseAdapter.insert
import de.kontranik.freebudget.database.DatabaseAdapter.deleteCategory
import de.kontranik.freebudget.fragment.AllTransactionFragment.changeShowOnlyPlanned
import de.kontranik.freebudget.service.SoftKeyboard.showKeyboard
import de.kontranik.freebudget.database.DatabaseAdapter.getRegularById
import de.kontranik.freebudget.model.RegularTransaction.description
import de.kontranik.freebudget.model.RegularTransaction.category
import de.kontranik.freebudget.model.RegularTransaction.day
import de.kontranik.freebudget.model.RegularTransaction.amount
import de.kontranik.freebudget.model.RegularTransaction.month
import de.kontranik.freebudget.model.RegularTransaction.date_start
import de.kontranik.freebudget.model.RegularTransaction.date_end
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import de.kontranik.freebudget.database.DatabaseAdapter.deleteRegularTransaction
import de.kontranik.freebudget.service.FileService.exportFileRegular
import de.kontranik.freebudget.service.FileService.exportFileTransaction
import de.kontranik.freebudget.service.BackupAndRestore.exportDB
import de.kontranik.freebudget.service.BackupAndRestore.importDB
import de.kontranik.freebudget.service.FileService.importFileRegular
import de.kontranik.freebudget.service.FileService.importFileTransaction
import de.kontranik.freebudget.database.DatabaseAdapter.getTransaction
import de.kontranik.freebudget.model.Transaction.description
import de.kontranik.freebudget.model.Transaction.category
import de.kontranik.freebudget.model.Transaction.amount_fact
import de.kontranik.freebudget.model.Transaction.amount_planned
import de.kontranik.freebudget.model.Transaction.date
import de.kontranik.freebudget.model.Transaction.regular_id
import de.kontranik.freebudget.database.DatabaseAdapter.deleteTransaction
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.database.DatabaseAdapter
import android.os.Bundle
import de.kontranik.freebudget.R
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import de.kontranik.freebudget.activity.CategoryListActivity
import android.app.Activity
import androidx.drawerlayout.widget.DrawerLayout
import de.kontranik.freebudget.fragment.AllTransactionFragment
import de.kontranik.freebudget.model.DrawerItem
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.adapter.DrawerItemCustomAdapter
import de.kontranik.freebudget.activity.MainActivity.DrawerItemClickListener
import de.kontranik.freebudget.fragment.OverviewFragment
import de.kontranik.freebudget.fragment.RegularFragment
import de.kontranik.freebudget.activity.ToolsActivity
import de.kontranik.freebudget.activity.SettingsActivity
import android.os.Build
import android.os.Environment
import de.kontranik.freebudget.activity.OpenFileActivity
import de.kontranik.freebudget.service.SoftKeyboard
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.activity.RegularTransactionActivity
import android.content.SharedPreferences
import de.kontranik.freebudget.service.FileService
import de.kontranik.freebudget.service.BackupAndRestore
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import de.kontranik.freebudget.activity.TransactionActivity
import java.lang.Exception
import java.text.DateFormat
import java.util.*

class RegularTransactionActivity : AppCompatActivity() {
    private var editTextDescription: EditText? = null
    private var acTextViewCategory: AutoCompleteTextView? = null
    private var editTextAmount: EditText? = null
    private var editTextDay: EditText? = null
    private var spinnerMonth: Spinner? = null
    private var button_start_date: Button? = null
    private var button_end_date: Button? = null
    private var buttonDelete: Button? = null
    private var buttonCopy: Button? = null
    private var radioButtonReceipts: RadioButton? = null
    private var radioButtonSpending: RadioButton? = null
    private var dbAdapter: DatabaseAdapter? = null
    private var transactionID: Long = 0
    private var date_start: Long = 0
    private var date_end: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regular_transaction)
        setTitle(R.string.new_regular_transaction)
        editTextDescription = findViewById<View>(R.id.editText_description_regular) as EditText
        editTextDescription!!.requestFocus()
        showKeyboard(this)
        acTextViewCategory =
            findViewById<View>(R.id.acTextView_category_regular) as AutoCompleteTextView
        editTextAmount = findViewById<View>(R.id.editText_amount_regular) as EditText
        editTextDay = findViewById<View>(R.id.editText_day_regular) as EditText
        spinnerMonth = findViewById<View>(R.id.spinner_Month_regular) as Spinner
        buttonDelete = findViewById<View>(R.id.button_delete_regular) as Button
        buttonCopy = findViewById<View>(R.id.button_copy_regular) as Button
        button_start_date = findViewById<View>(R.id.button_start_date) as Button
        button_end_date = findViewById<View>(R.id.button_end_date) as Button
        val imageButton_clear_start_date =
            findViewById<View>(R.id.imageButton_clear_start_date) as ImageButton
        val imageButton_clear_end_date =
            findViewById<View>(R.id.imageButton_clear_end_date) as ImageButton
        dbAdapter = DatabaseAdapter(this)
        radioButtonReceipts = findViewById<View>(R.id.radioButton_receipts_regular) as RadioButton
        radioButtonSpending = findViewById<View>(R.id.radioButton_spending_regular) as RadioButton

        // perform click event on edit text
        button_start_date!!.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val newCalendar = Calendar.getInstance()
            if (date_start == 0L) {
                date_start = newCalendar.timeInMillis
            } else {
                newCalendar.timeInMillis = date_start
            }
            val datePickerDialog = DatePickerDialog(
                this@RegularTransactionActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val datePickerDate = Calendar.getInstance()
                    datePickerDate.clear()
                    datePickerDate[year, monthOfYear, dayOfMonth, 0, 0] = 1
                    datePickerDate[Calendar.MILLISECOND] = 0
                    date_start = datePickerDate.timeInMillis
                    setDateText(button_start_date, date_start)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }

        // perform click event on edit text
        button_end_date!!.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val newCalendar = Calendar.getInstance()
            if (date_end == 0L) {
                date_end = newCalendar.timeInMillis
            } else {
                newCalendar.timeInMillis = date_end
            }
            val datePickerDialog = DatePickerDialog(
                this@RegularTransactionActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val datePickerDate = Calendar.getInstance()
                    datePickerDate[year, monthOfYear, dayOfMonth, 23, 59] = 59
                    datePickerDate[Calendar.MILLISECOND] = 999
                    date_end = datePickerDate.timeInMillis
                    setDateText(button_end_date, date_end)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }
        imageButton_clear_start_date.setOnClickListener {
            date_start = 0
            button_start_date!!.setText(R.string.not_set)
        }
        imageButton_clear_end_date.setOnClickListener {
            date_end = 0
            button_end_date!!.setText(R.string.not_set)
        }
        dbAdapter!!.open()
        val categoryArrayList = dbAdapter!!.allCategory
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categoryArrayList
        )
        acTextViewCategory!!.setAdapter(adapter)
        acTextViewCategory!!.onItemClickListener =
            OnItemClickListener { arg0, arg1, arg2, arg3 -> // Category selected = (Category) arg0.getAdapter().getItem(arg2);
                /*
                    Toast.makeText(RegularTransactionActivity.this,
                            "Clicked " + arg2 + " name: " + selected.getName(),
                            Toast.LENGTH_SHORT).show();
                    */
            }

        // get ID from Buffer
        val extras = intent.extras
        if (extras != null) {
            transactionID = extras.getLong(TRANS_ID)
        }
        // if ID = 0, then create new transaction; else update
        spinnerMonth!!.setSelection(0)
        if (transactionID > 0) {
            // получаем элемент по id из бд
            dbAdapter!!.open()
            val transaction = dbAdapter!!.getRegularById(transactionID)
            if (transaction != null) {
                editTextDescription!!.setText(transaction.description)
                acTextViewCategory!!.setText(transaction.category)
                editTextDay!!.setText(java.lang.String.valueOf(transaction.day))
                if (transaction.amount != 0) {
                    editTextAmount!!.setText(Math.abs(transaction.amount).toString())
                }
                if (transaction.amount > 0) {
                    radioButtonReceipts!!.isChecked = true
                } else if (transaction.amount < 0) {
                    radioButtonSpending!!.isChecked = true
                }
                spinnerMonth!!.setSelection(transaction.month)
                date_start = transaction.date_start
                date_end = transaction.date_end
            }
            dbAdapter!!.close()
        } else {
            var transStat: String? = null
            if (extras != null) {
                if (extras.containsKey(TRANS_STAT)) transStat = extras.getString(TRANS_STAT)
                if (extras.containsKey(MONTH)) spinnerMonth!!.setSelection(extras.getInt(MONTH))
            }
            if (transStat != null) {
                if (transStat == TRANS_STAT_PLUS) {
                    radioButtonReceipts!!.isChecked = true
                } else {
                    radioButtonSpending!!.isChecked = true
                }
            }

            // hide delete button
            buttonDelete!!.visibility = View.GONE
            date_start = 0
            date_end = 0
        }
        setDateText(button_start_date, date_start)
        setDateText(button_end_date, date_end)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(REGULAR_DATE_START, date_start)
        outState.putLong(REGULAR_DATE_END, date_end)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(REGULAR_DATE_START)) {
                date_start = savedInstanceState.getLong(REGULAR_DATE_START)
                setDateText(button_start_date, date_start)
            }
            if (savedInstanceState.containsKey(REGULAR_DATE_END)) {
                date_end = savedInstanceState.getLong(REGULAR_DATE_END)
                setDateText(button_end_date, date_end)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                acTextViewCategory!!.setText(data!!.getStringExtra(CategoryListActivity.Companion.RESULT_CATEGORY))
            }
        }
    }

    fun saveAndClose(view: View?) {
        save(view)
        hideKeyboard(this)
        finish()
        //goHome();
    }

    fun save(view: View?) {
        val description = editTextDescription!!.text.toString()
        val categoryName = acTextViewCategory!!.text.toString()
        var amount: Double
        try {
            amount = editTextAmount!!.text.toString().toDouble()
            if (radioButtonReceipts!!.isChecked) {
                amount = Math.abs(amount)
            } else if (radioButtonSpending!!.isChecked) {
                amount = 0 - Math.abs(amount)
            }
        } catch (e: Exception) {
            amount = 0.0
        }
        val month = spinnerMonth!!.selectedItemPosition
        val day = editTextDay!!.text.toString().toInt()
        dbAdapter!!.open()
        val regularTransaction =
            RegularTransaction(transactionID, month, day, description, categoryName, amount)
        regularTransaction.date_start = date_start
        regularTransaction.date_end = date_end
        if (transactionID > 0) {
            dbAdapter!!.update(regularTransaction)
        } else {
            dbAdapter!!.insert(regularTransaction)
        }
        dbAdapter!!.close()
    }

    fun copy(view: View?) {
        transactionID = 0
        buttonDelete!!.visibility = View.GONE
        buttonCopy!!.visibility = View.GONE
    }

    fun delete(view: View?) {
        dbAdapter!!.open()
        dbAdapter!!.deleteRegularTransaction(transactionID)
        dbAdapter!!.close()
        hideKeyboard(this)
        finish()
    }

    fun selectCat(view: View?) {
        val intent = Intent(this, CategoryListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivityForResult(intent, PICK_CATEGORY_REQUEST)
    }

    private fun setDateText(button: Button?, date: Long) {
        if (date == 0L) {
            button!!.setText(R.string.not_set)
        } else {
            val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            button!!.text = dateFormatter.format(date)
        }
    }

    companion object {
        const val MONTH = "MONTH"
        const val PICK_CATEGORY_REQUEST = 123 // The request code
        const val REGULAR_DATE_START = "date_start"
        const val REGULAR_DATE_END = "date_end"
    }
}