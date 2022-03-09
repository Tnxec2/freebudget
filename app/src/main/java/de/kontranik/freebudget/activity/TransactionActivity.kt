package de.kontranik.freebudget.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.DatabaseAdapter
import de.kontranik.freebudget.databinding.ActivityTransactionBinding
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import de.kontranik.freebudget.service.SoftKeyboard.showKeyboard
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding

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
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextDescription.requestFocus()
        showKeyboard(this)

        // initiate the date picker and a button
        dbAdapter = DatabaseAdapter(this)
        dbAdapter!!.open()
        val categoryArrayList = dbAdapter!!.allCategory
        dbAdapter!!.close()
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categoryArrayList
        )
        binding.acTextViewCategory.setAdapter(adapter)
        binding.acTextViewCategory.onItemClickListener =
            OnItemClickListener { arg0, arg1, arg2, arg3 -> // Category selected = (Category) arg0.getAdapter().getItem(arg2);
                /*
                    Toast.makeText(RegularTransactionActivity.this,
                            "Clicked " + arg2 + " name: " + selected.getName(),
                            Toast.LENGTH_SHORT).show();
                    */
            }

        // perform click event on edit text
        binding.buttonDate.setOnClickListener { // calender class's instance and get current date , month and year from calender
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
            if (extras.containsKey(Constant.TRANS_ID)) transactionID = extras.getLong(Constant.TRANS_ID)
            if (extras.containsKey(Constant.TRANS_TYP)) planned =
                extras.getString(Constant.TRANS_TYP) == Constant.TRANS_TYP_PLANNED
        }
        //
        val displayDate: Long
        if (transactionID > 0) {
            // get entry from db
            dbAdapter!!.open()
            val transaction = dbAdapter!!.getTransaction(transactionID)
            binding.editTextDescription.setText(transaction!!.description)
            binding.acTextViewCategory.setText(transaction.category)
            if (transaction.amount_fact != 0.0) {
                binding.editTextAmountFact.setText(abs(transaction.amount_fact).toString())
            }
            if (transaction.amount_planned != 0.0) {
                binding.editTextAmountPlanned.setText(abs(transaction.amount_planned).toString())
            }
            displayDate = if (transaction.date > 0) transaction.date else Date().time
            when {
                transaction.amount_fact > 0 -> {
                    binding.radioButtonReceipts.isChecked = true
                }
                transaction.amount_fact < 0 -> {
                    binding.radioButtonSpending.isChecked = true
                }
                transaction.amount_planned > 0 -> {
                    binding.radioButtonReceipts.isChecked = true
                }
                transaction.amount_planned < 0 -> {
                    binding.radioButtonSpending.isChecked = true
                }
            }
            dbAdapter!!.close()
        } else {
            var transStat: String? = Constant.TRANS_STAT_MINUS
            if (extras != null) {
                if (extras.containsKey(Constant.TRANS_STAT)) transStat = extras.getString(Constant.TRANS_STAT)
            }
            if (transStat != null && transStat == Constant.TRANS_STAT_PLUS) {
                binding.radioButtonReceipts.isChecked = true
            } else {
                binding.radioButtonSpending.isChecked = true
            }
            displayDate = Date().time

            // hide delete button
            binding.buttonDelete.visibility = View.GONE
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
        binding.editTextAmountPlanned.isEnabled = planned
        binding.editTextAmountFact.isEnabled = !planned
        binding.btnCopyAmount.isEnabled = !planned
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                binding.acTextViewCategory.setText(data!!.getStringExtra(CategoryListActivity.Companion.RESULT_CATEGORY))
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
        binding.buttonDate.text = df.format(cal.timeInMillis)
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
        val description = binding.editTextDescription.text.toString()
        val categoryName = binding.acTextViewCategory.text.toString()
        var amountPlanned: Double
        var amountFact: Double
        try {
            amountPlanned = binding.editTextAmountPlanned.text.toString().toDouble()
            if (binding.radioButtonReceipts.isChecked) {
                amountPlanned = Math.abs(amountPlanned)
            } else if (binding.radioButtonSpending.isChecked) {
                amountPlanned = 0 - Math.abs(amountPlanned)
            }
        } catch (e: Exception) {
            amountPlanned = 0.0
        }
        try {
            amountFact = binding.editTextAmountFact.text.toString().toDouble()
            if (binding.radioButtonReceipts.isChecked) {
                amountFact = abs(amountFact)
            } else if (binding.radioButtonSpending.isChecked) {
                amountFact = 0 - abs(amountFact)
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
        binding.buttonDelete.visibility = View.GONE
        binding.buttonCopy.visibility = View.GONE
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
        binding.editTextAmountFact.text = binding.editTextAmountPlanned.text
    }

    companion object {
        const val PICK_CATEGORY_REQUEST = 123 // The request code
    }
}