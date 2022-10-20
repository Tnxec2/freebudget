package de.kontranik.freebudget.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.databinding.ActivityRegularTransactionBinding
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

class RegularTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegularTransactionBinding
    private lateinit var mRegularTransactionViewModel: RegularTransactionViewModel
    private lateinit var mCategoryViewModel: CategoryViewModel

    private var transactionID: Long? = null
    private var dateStart: Long? = null
    private var dateEnd: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegularTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.new_regular_transaction)
        binding.editTextDescriptionRegular.requestFocus()
        //showKeyboard(this)

        mRegularTransactionViewModel = ViewModelProvider(this)[RegularTransactionViewModel::class.java]
        mCategoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding.buttonSaveAndCloseRegular.setOnClickListener { saveAndClose() }
        binding.buttonCloseRegular.setOnClickListener { close() }
        binding.buttonSaveRegular.setOnClickListener { save() }
        binding.buttonDeleteRegular.setOnClickListener { delete() }
        binding.buttonCopyRegular.setOnClickListener { copy() }
        binding.btnSelectCatRegular.setOnClickListener { selectCat() }

        // perform click event on edit text
        binding.buttonStartDate.setOnClickListener { // calender class's instance and get current date , month and year from calender
            val newCalendar = Calendar.getInstance()
            if (dateStart == null) {
                dateStart = newCalendar.timeInMillis
            } else {
                newCalendar.timeInMillis = dateStart!!
            }
            val datePickerDialog = DatePickerDialog(
                this@RegularTransactionActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val datePickerDate = Calendar.getInstance()
                    datePickerDate.clear()
                    datePickerDate[year, monthOfYear, dayOfMonth, 0, 0] = 1
                    datePickerDate[Calendar.MILLISECOND] = 0
                    dateStart = datePickerDate.timeInMillis
                    setDateText(binding.buttonStartDate, dateStart!!)
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
            if (dateEnd == null) {
                dateEnd = newCalendar.timeInMillis
            } else {
                newCalendar.timeInMillis = dateEnd!!
            }
            val datePickerDialog = DatePickerDialog(
                this@RegularTransactionActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val datePickerDate = Calendar.getInstance()
                    datePickerDate[year, monthOfYear, dayOfMonth, 23, 59] = 59
                    datePickerDate[Calendar.MILLISECOND] = 999
                    dateEnd = datePickerDate.timeInMillis
                    setDateText(binding.buttonEndDate, dateEnd!!)
                },
                newCalendar[Calendar.YEAR],
                newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }
        binding.imageButtonClearStartDate.setOnClickListener {
            dateStart = 0
            binding.buttonStartDate.setText(R.string.not_setted)
        }
        binding.imageButtonClearEndDate.setOnClickListener {
            dateEnd = 0
            binding.buttonEndDate.setText(R.string.not_setted)
        }

        val categoryArrayList: MutableList<Category> = mutableListOf()
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
        transactionID = if (extras != null && extras.containsKey(Constant.TRANS_ID)) {
            extras.getLong(Constant.TRANS_ID)
        } else {
            null
        }
        Log.d("NIK", "${if (transactionID!=null) transactionID else "NULL"}")
        mCategoryViewModel.mAllCategorys.observe(this) {
            categoryArrayList.clear()
            categoryArrayList.addAll(it)
            adapter.notifyDataSetChanged()
        }

        mRegularTransactionViewModel.regularTransactionById.observe(this){
            if (it != null) {
                binding.editTextDescriptionRegular.setText(it.description)
                binding.acTextViewCategoryRegular.setText(it.category)
                binding.editTextDayRegular.setText(java.lang.String.valueOf(it.day))
                if (it.amount != 0.0) {
                    binding.editTextAmountRegular.setText(abs(it.amount).toString())
                }
                if (it.amount > 0) {
                    binding.radioButtonReceiptsRegular.isChecked = true
                } else if (it.amount < 0) {
                    binding.radioButtonSpendingRegular.isChecked = true
                }
                binding.spinnerMonthRegular.setSelection(it.month)
                dateStart = it.dateStart
                dateEnd = it.dateEnd
                setDateText(binding.buttonStartDate, dateStart)
                setDateText(binding.buttonEndDate, dateEnd)
                binding.editTextNoteRegular.setText(it.note)
            }
        }

        // if ID = 0, then create new transaction; else update
        binding.spinnerMonthRegular.setSelection(0)
        if (transactionID != null) {
            mRegularTransactionViewModel.loadRegularTransactionsById(transactionID!!)
        } else {
            mRegularTransactionViewModel.clearRegularTransactionsById()
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
            dateStart = null
            dateEnd = null
            setDateText(binding.buttonStartDate, dateStart)
            setDateText(binding.buttonEndDate, dateEnd)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (dateStart != null) outState.putLong(REGULAR_DATE_START, dateStart!!)
        else outState.remove(REGULAR_DATE_START)
        if (dateEnd != null) outState.putLong(REGULAR_DATE_END, dateEnd!!)
        else outState.remove(REGULAR_DATE_END)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        dateStart = if (savedInstanceState.containsKey(REGULAR_DATE_START)) {
            savedInstanceState.getLong(REGULAR_DATE_START)
        } else {
            null
        }
        setDateText(binding.buttonStartDate, dateStart)
        dateEnd = if (savedInstanceState.containsKey(REGULAR_DATE_END)) {
            savedInstanceState.getLong(REGULAR_DATE_END)
        } else {
            null
        }
        setDateText(binding.buttonEndDate, dateEnd)
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

    private fun saveAndClose() {
        save()
        close()
    }

    private fun close() {
        hideKeyboard(this)
        finish()
        //goHome();
    }

    private fun save() {
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
        val note = binding.editTextNoteRegular.text.toString()

        val regularTransaction =
            RegularTransaction(transactionID, month, day, description, categoryName, amount, note)
        regularTransaction.dateStart = dateStart
        regularTransaction.dateEnd = dateEnd
        if (transactionID != null) {
            mRegularTransactionViewModel.update(regularTransaction)
        } else {
            mRegularTransactionViewModel.insert(regularTransaction)
        }
    }

    private fun copy() {
        transactionID = null
        binding.buttonDeleteRegular.visibility = View.GONE
        binding.buttonCopyRegular.visibility = View.GONE
    }

    private fun delete() {
        transactionID?.let { mRegularTransactionViewModel.delete(it) }
        hideKeyboard(this)
        finish()
    }

    private fun selectCat() {
        val intent = Intent(this, CategoryListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivityForResult(intent, PICK_CATEGORY_REQUEST)
    }

    private fun setDateText(button: Button?, date: Long?) {
        if (date == null) {
            button!!.setText(R.string.not_setted)
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