package de.kontranik.freebudget.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.CategoryListActivity
import de.kontranik.freebudget.database.DatabaseAdapter
import de.kontranik.freebudget.databinding.ActivityRegularTransactionBinding
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import de.kontranik.freebudget.service.SoftKeyboard.showKeyboard
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

class RegularTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegularTransactionBinding

    private var dbAdapter: DatabaseAdapter? = null
    private var transactionID: Long = 0
    private var date_start: Long = 0
    private var date_end: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegularTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.new_regular_transaction)
        binding.editTextDescriptionRegular.requestFocus()
        showKeyboard(this)

        dbAdapter = DatabaseAdapter(this)

        // perform click event on edit text
        binding.buttonStartDate.setOnClickListener { // calender class's instance and get current date , month and year from calender
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
                    setDateText(binding.buttonStartDate, date_start)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }

        // perform click event on edit text
        binding.buttonEndDate.setOnClickListener { // calender class's instance and get current date , month and year from calender
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
                    setDateText(binding.buttonEndDate, date_end)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }
        binding.imageButtonClearStartDate.setOnClickListener {
            date_start = 0
            binding.buttonStartDate.setText(R.string.not_set)
        }
        binding.imageButtonClearEndDate.setOnClickListener {
            date_end = 0
            binding.buttonEndDate.setText(R.string.not_set)
        }
        dbAdapter!!.open()
        val categoryArrayList = dbAdapter!!.allCategory
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categoryArrayList
        )
        binding.acTextViewCategoryRegular.setAdapter(adapter)
        binding.acTextViewCategoryRegular.onItemClickListener =
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
            transactionID = extras.getLong(Constant.TRANS_ID)
        }
        // if ID = 0, then create new transaction; else update
        binding.spinnerMonthRegular.setSelection(0)
        if (transactionID > 0) {
            // получаем элемент по id из бд
            dbAdapter!!.open()
            val transaction = dbAdapter!!.getRegularById(transactionID)
            if (transaction != null) {
                binding.editTextDescriptionRegular.setText(transaction.description)
                binding.acTextViewCategoryRegular.setText(transaction.category)
                binding.editTextDayRegular.setText(java.lang.String.valueOf(transaction.day))
                if (transaction.amount != 0.0) {
                    binding.editTextAmountRegular.setText(abs(transaction.amount).toString())
                }
                if (transaction.amount > 0) {
                    binding.radioButtonReceiptsRegular.isChecked = true
                } else if (transaction.amount < 0) {
                    binding.radioButtonSpendingRegular.isChecked = true
                }
                binding.spinnerMonthRegular.setSelection(transaction.month)
                date_start = transaction.date_start
                date_end = transaction.date_end
            }
            dbAdapter!!.close()
        } else {
            var transStat: String? = null
            if (extras != null) {
                if (extras.containsKey(Constant.TRANS_STAT)) transStat = extras.getString(Constant.TRANS_STAT )
                if (extras.containsKey(MONTH)) binding.spinnerMonthRegular.setSelection(extras.getInt(MONTH))
            }
            if (transStat != null) {
                if (transStat == Constant.TRANS_STAT_PLUS) {
                    binding.radioButtonReceiptsRegular.isChecked = true
                } else {
                    binding.radioButtonSpendingRegular.isChecked = true
                }
            }

            // hide delete button
            binding.buttonDeleteRegular.visibility = View.GONE
            date_start = 0
            date_end = 0
        }
        setDateText(binding.buttonStartDate, date_start)
        setDateText(binding.buttonEndDate, date_end)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(REGULAR_DATE_START, date_start)
        outState.putLong(REGULAR_DATE_END, date_end)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(REGULAR_DATE_START)) {
            date_start = savedInstanceState.getLong(REGULAR_DATE_START)
            setDateText(binding.buttonStartDate, date_start)
        }
        if (savedInstanceState.containsKey(REGULAR_DATE_END)) {
            date_end = savedInstanceState.getLong(REGULAR_DATE_END)
            setDateText(binding.buttonEndDate, date_end)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                binding.acTextViewCategoryRegular.setText(data!!.getStringExtra(CategoryListActivity.Companion.RESULT_CATEGORY))
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
        val description = binding.editTextDescriptionRegular.text.toString()
        val categoryName = binding.acTextViewCategoryRegular.text.toString()
        var amount: Double
        try {
            amount = binding.editTextAmountRegular.text.toString().toDouble()
            if (binding.radioButtonReceiptsRegular.isChecked) {
                amount = abs(amount)
            } else if (binding.radioButtonSpendingRegular.isChecked) {
                amount = 0 - abs(amount)
            }
        } catch (e: Exception) {
            amount = 0.0
        }
        val month = binding.spinnerMonthRegular.selectedItemPosition
        val day = binding.editTextDayRegular.text.toString().toInt()
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
        binding.buttonDeleteRegular.visibility = View.GONE
        binding.buttonCopyRegular.visibility = View.GONE
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