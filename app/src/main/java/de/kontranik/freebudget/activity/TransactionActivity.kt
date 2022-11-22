package de.kontranik.freebudget.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.databinding.ActivityTransactionBinding
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.service.Constant
import de.kontranik.freebudget.service.SoftKeyboard.hideKeyboard
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var mTransactionViewModel: TransactionViewModel
    private lateinit var mCategoryViewModel: CategoryViewModel

    private var transactionID: Long? = null
    private var regularCreateTime: Long? = null

    private var year = 0
    var month = 0
    var day = 0
    private var planned = false
    private var dbentryAmountPlanned: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.title_activity_transaction)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mTransactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        mCategoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding.editTextDescription.requestFocus()
        //showKeyboard(this)

        binding.buttonCopy.setOnClickListener { copy() }
        binding.buttonSave.setOnClickListener { save() }
        binding.buttonSaveAndClose.setOnClickListener { saveAndClose() }
        binding.buttonClose.setOnClickListener { close() }
        binding.buttonDelete.setOnClickListener { delete() }
        binding.btnCopyAmount.setOnClickListener { copyAmount() }
        binding.btnSelectCat.setOnClickListener { selectCat() }

        val categoryArrayList: MutableList<Category> = mutableListOf()

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categoryArrayList
        )
        binding.acTextViewCategory.setAdapter(adapter)
        binding.acTextViewCategory.onItemClickListener =
            OnItemClickListener { _, _, _, _ -> // Category selected = (Category) arg0.getAdapter().getItem(arg2);
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
            val datePickerDialog = DatePickerDialog(this@TransactionActivity,
                { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    setDateBox(year, monthOfYear + 1, dayOfMonth)
                }, year, month - 1, day
            )
            datePickerDialog.show()
        }
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(Constant.TRANS_ID)) transactionID = extras.getLong(Constant.TRANS_ID)
            if (extras.containsKey(Constant.TRANS_TYP)) planned =
                extras.getString(Constant.TRANS_TYP) == Constant.TRANS_TYP_PLANNED
        }

        mCategoryViewModel.mAllCategorys.observe(this) {
            categoryArrayList.clear()
            categoryArrayList.addAll(it)
            adapter.notifyDataSetChanged()
        }

        mTransactionViewModel.transactionById.observe(this) {
            regularCreateTime = it?.regularCreateTime
            refreshView(it)
        }

        if (transactionID != null) {
            // get entry from db
            mTransactionViewModel.loadById(transactionID!!)
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
            val displayDate = Date().time
            val calendar = Calendar.getInstance()
            calendar.time = Date(displayDate)
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH] + 1
            day = calendar[Calendar.DAY_OF_MONTH]
            setDateBox(year, month, day)
            // hide delete button
            binding.buttonDelete.visibility = View.GONE
        }

        binding.editTextAmountPlanned.isEnabled = planned
        binding.editTextAmountFact.isEnabled = !planned
        binding.btnCopyAmount.isEnabled = !planned


    }

    private fun refreshView(transaction: Transaction?) {
        if (transaction == null) return

        dbentryAmountPlanned = transaction.amountPlanned

        binding.editTextDescription.setText(transaction.description)
        binding.acTextViewCategory.setText(transaction.category)
        if (transaction.amountFact != 0.0) {
            binding.editTextAmountFact.setText(abs(transaction.amountFact).toString())
        }
        if (transaction.amountPlanned != 0.0) {
            binding.editTextAmountPlanned.setText(abs(transaction.amountPlanned).toString())
        }

        binding.radioButtonReceipts.isChecked = ( transaction.amountFact > 0 || (transaction.amountFact == 0.0 && transaction.amountPlanned >= 0))
        binding.radioButtonSpending.isChecked = ( transaction.amountFact < 0 || (transaction.amountFact == 0.0 && transaction.amountPlanned < 0))


        val displayDate = if (transaction.date > 0) transaction.date else Date().time
        if (displayDate > 0) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(displayDate)
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH] + 1
            day = calendar[Calendar.DAY_OF_MONTH]
        }
        setDateBox(year, month, day)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CATEGORY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                binding.acTextViewCategory.setText(data!!.getStringExtra(CategoryListActivity.RESULT_CATEGORY))
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
        val description = binding.editTextDescription.text.toString()
        val note = binding.editTextNote.text.toString()
        val categoryName = binding.acTextViewCategory.text.toString()
        var amountPlanned: Double
        try {
            amountPlanned = binding.editTextAmountPlanned.text.toString().toDouble()
            if (binding.radioButtonReceipts.isChecked) {
                amountPlanned = abs(amountPlanned)
            } else if (binding.radioButtonSpending.isChecked) {
                amountPlanned = 0 - abs(amountPlanned)
            }
        } catch (e: Exception) {
            amountPlanned = 0.0
        }
        var amountFact: Double
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

        val entry = Transaction(
            id = transactionID,
            regularCreateTime = regularCreateTime,
            description = description,
            category = categoryName,
            date = cal.timeInMillis,
            amountPlanned = if (planned) amountPlanned else dbentryAmountPlanned ,
            amountFact = amountFact,
            note = note
        )

        if (transactionID != null) {
            mTransactionViewModel.update(entry)
        } else {
            mTransactionViewModel.insert(entry)
        }
    }

    private fun copy() {
        transactionID = null
        binding.buttonDelete.visibility = View.GONE
        binding.buttonCopy.visibility = View.GONE
    }

    private fun delete() {
        transactionID?.let { mTransactionViewModel.delete(it) }
        hideKeyboard(this)
        finish()
        //goHome();
    }

    private fun selectCat() {
        val intent = Intent(this, CategoryListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivityForResult(intent, PICK_CATEGORY_REQUEST)
    }

    private fun copyAmount() {
        binding.editTextAmountFact.text = binding.editTextAmountPlanned.text
    }

    companion object {
        const val PICK_CATEGORY_REQUEST = 123 // The request code
    }
}