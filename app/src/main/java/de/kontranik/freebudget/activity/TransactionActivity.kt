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
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import de.kontranik.freebudget.activity.TransactionActivity
import de.kontranik.freebudget.model.Transaction
import java.lang.Exception
import java.text.DateFormat
import java.util.*

class TransactionActivity : AppCompatActivity() {
    private var editTextDescription: EditText? = null
    private var acTextViewCategory: AutoCompleteTextView? = null
    private var editTextAmountPlanned: EditText? = null
    private var editTextAmountFact: EditText? = null
    private var button_date: Button? = null
    private var buttonDelete: Button? = null
    private var buttonCopy: Button? = null
    private var buttonCopyAmount: ImageButton? = null
    private var radioButtonReceipts: RadioButton? = null
    private var radioButtonSpending: RadioButton? = null
    private var dbAdapter: DatabaseAdapter? = null
    private var transactionID: Long = 0
    var datePickerDialog: DatePickerDialog? = null
    var year = 0
    var month = 0
    var day = 0
    var planned = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.title_activity_transaction)
        setContentView(R.layout.activity_transaction)
        editTextDescription = findViewById<View>(R.id.editText_description) as EditText
        editTextDescription!!.requestFocus()
        showKeyboard(this)
        acTextViewCategory = findViewById<View>(R.id.acTextView_category) as AutoCompleteTextView
        editTextAmountPlanned = findViewById<View>(R.id.editText_amount_planned) as EditText
        editTextAmountFact = findViewById<View>(R.id.editText_amount_fact) as EditText
        buttonDelete = findViewById<View>(R.id.button_delete) as Button
        buttonCopy = findViewById<View>(R.id.button_copy) as Button
        buttonCopyAmount = findViewById<View>(R.id.btn_copy_amount) as ImageButton

        // initiate the date picker and a button
        button_date = findViewById<View>(R.id.button_date) as Button
        dbAdapter = DatabaseAdapter(this)
        dbAdapter!!.open()
        val categoryArrayList = dbAdapter!!.allCategory
        dbAdapter!!.close()
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
        radioButtonReceipts = findViewById<View>(R.id.radioButton_receipts) as RadioButton
        radioButtonSpending = findViewById<View>(R.id.radioButton_spending) as RadioButton

        // perform click event on edit text
        button_date!!.setOnClickListener { // calender class's instance and get current date , month and year from calender
            if (day == 0) {
                val c = Calendar.getInstance()
                year = c[Calendar.YEAR] // current year
                month = c[Calendar.MONTH] + 1 // current month
                day = c[Calendar.DAY_OF_MONTH] // current day
            }
            // date picker dialog
            datePickerDialog = DatePickerDialog(this@TransactionActivity,
                { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    setDateBox(year, monthOfYear + 1, dayOfMonth)
                }, year, month - 1, day
            )
            datePickerDialog!!.show()
        }
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(TRANS_ID)) transactionID = extras.getLong(TRANS_ID)
            if (extras.containsKey(TRANS_TYP)) planned =
                extras.getString(TRANS_TYP) == TRANS_TYP_PLANNED
        }
        //
        val displayDate: Long
        if (transactionID > 0) {
            // get entry from db
            dbAdapter!!.open()
            val transaction = dbAdapter!!.getTransaction(transactionID)
            editTextDescription!!.setText(transaction!!.description)
            acTextViewCategory!!.setText(transaction.category)
            if (transaction.amount_fact != 0.0) {
                editTextAmountFact!!.setText(Math.abs(transaction.amount_fact).toString())
            }
            if (transaction.amount_planned != 0.0) {
                editTextAmountPlanned!!.setText(Math.abs(transaction.amount_planned).toString())
            }
            displayDate = if (transaction.date > 0) transaction.date else Date().time
            if (transaction.amount_fact > 0) {
                radioButtonReceipts!!.isChecked = true
            } else if (transaction.amount_fact < 0) {
                radioButtonSpending!!.isChecked = true
            } else if (transaction.amount_planned > 0) {
                radioButtonReceipts!!.isChecked = true
            } else if (transaction.amount_planned < 0) {
                radioButtonSpending!!.isChecked = true
            }
            dbAdapter!!.close()
        } else {
            var transStat: String? = TRANS_STAT_MINUS
            if (extras != null) {
                if (extras.containsKey(TRANS_STAT)) transStat = extras.getString(TRANS_STAT)
            }
            if (transStat != null && transStat == TRANS_STAT_PLUS) {
                radioButtonReceipts!!.isChecked = true
            } else {
                radioButtonSpending!!.isChecked = true
            }
            displayDate = Date().time

            // hide delete button
            buttonDelete!!.visibility = View.GONE
            Log.d("NIK", planned.toString())
        }
        if (displayDate > 0) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(displayDate)
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH] + 1
            day = calendar[Calendar.DAY_OF_MONTH]
        }
        setDateBox(year, month, day)
        editTextAmountPlanned!!.isEnabled = planned
        editTextAmountFact!!.isEnabled = !planned
        buttonCopyAmount!!.isEnabled = !planned
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                acTextViewCategory!!.setText(data!!.getStringExtra(CategoryListActivity.Companion.RESULT_CATEGORY))
            }
        }
    }

    private fun setDateBox(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        val cal = Calendar.getInstance()
        cal[year, month - 1] = day
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        button_date!!.text = df.format(cal.timeInMillis)
    }

    // find Index of Spinner by Value
    private fun getIndex(spinner: Spinner, myString: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                index = i
                break
            }
        }
        return index
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
        var amountPlanned: Double
        var amountFact: Double
        try {
            amountPlanned = editTextAmountPlanned!!.text.toString().toDouble()
            if (radioButtonReceipts!!.isChecked) {
                amountPlanned = Math.abs(amountPlanned)
            } else if (radioButtonSpending!!.isChecked) {
                amountPlanned = 0 - Math.abs(amountPlanned)
            }
        } catch (e: Exception) {
            amountPlanned = 0.0
        }
        try {
            amountFact = editTextAmountFact!!.text.toString().toDouble()
            if (radioButtonReceipts!!.isChecked) {
                amountFact = Math.abs(amountFact)
            } else if (radioButtonSpending!!.isChecked) {
                amountFact = 0 - Math.abs(amountFact)
            }
        } catch (e: Exception) {
            amountFact = 0.0
        }
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month - 1
        cal[Calendar.DAY_OF_MONTH] = day
        dbAdapter!!.open()
        val entry = Transaction(
            transactionID,
            0.toLong(),
            description,
            categoryName,
            cal.timeInMillis,
            amountPlanned,
            amountFact
        )
        if (planned) {
            entry.amount_planned = amountPlanned
        } else {
            entry.amount_fact = amountFact
            if (transactionID > 0) {
                val dbentry = dbAdapter!!.getTransaction(transactionID)
                if (dbentry != null) {
                    entry.regular_id = dbentry.regular_id
                    entry.amount_planned = dbentry.amount_planned
                }
            }
        }
        if (transactionID > 0) {
            dbAdapter!!.update(entry)
        } else {
            dbAdapter!!.insert(entry)
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
        dbAdapter!!.deleteTransaction(transactionID)
        dbAdapter!!.close()
        hideKeyboard(this)
        finish()
        //goHome();
    }

    fun selectCat(view: View?) {
        val intent = Intent(this, CategoryListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivityForResult(intent, PICK_CATEGORY_REQUEST)
    }

    fun copyAmount(view: View?) {
        editTextAmountFact!!.text = editTextAmountPlanned!!.text
    }

    companion object {
        const val PICK_CATEGORY_REQUEST = 123 // The request code
    }
}