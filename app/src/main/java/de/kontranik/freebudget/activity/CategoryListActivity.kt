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
import de.kontranik.freebudget.model.Category
import java.util.ArrayList

class CategoryListActivity : AppCompatActivity() {
    private var listViewCategory: ListView? = null
    private var editTextCategory: EditText? = null
    private var btn_Save: Button? = null
    private var btn_Close: Button? = null
    private var btn_Delete: Button? = null
    private var category: Category? = null
    private var dbAdapter: DatabaseAdapter? = null
    private var categoryList: MutableList<Category?>? = null
    private var categoryArrayAdapter: ArrayAdapter<Category?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)
        categoryList = ArrayList()
        setTitle(R.string.activity_category)
        listViewCategory = findViewById<View>(R.id.listView_categoryList) as ListView
        editTextCategory = findViewById<View>(R.id.editText_categoryName) as EditText
        btn_Save = findViewById<View>(R.id.btn_categorySave) as Button
        btn_Close = findViewById<View>(R.id.btn_categoryClose) as Button
        btn_Delete = findViewById<View>(R.id.btn_categoryDelete) as Button
        btn_Save!!.setOnClickListener { v -> onSave(v) }
        btn_Close!!.setOnClickListener { v -> onClose(v) }
        btn_Delete!!.setOnClickListener { v -> onDelete(v) }
        dbAdapter = DatabaseAdapter(this)
        categoryArrayAdapter = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_list_item_1, categoryList
        )
        // устанавливаем для списка адаптер
        listViewCategory!!.adapter = categoryArrayAdapter
        listViewCategory!!.onItemClickListener =
            OnItemClickListener { parent, v, position, id -> // по позиции получаем выбранный элемент
                val selectedItem = categoryList.get(position)!!.name
                // установка текста элемента TextView
                editTextCategory!!.setText(selectedItem)
                dbAdapter!!.open()
                category = dbAdapter!!.getCategory(selectedItem)
                dbAdapter!!.close()
            }
    }

    override fun onResume() {
        super.onResume()
        getCategory()
    }

    fun getCategory() {
        dbAdapter!!.open()
        categoryList!!.clear()
        categoryList!!.addAll(dbAdapter!!.allCategory)
        dbAdapter!!.close()
        categoryArrayAdapter!!.notifyDataSetChanged()
    }

    fun onSave(view: View?) {
        dbAdapter!!.open()
        if (category == null) {
            category = Category(0, editTextCategory!!.text.toString())
        } else {
            category!!.name = editTextCategory!!.text.toString()
        }
        if (category!!.id > 0) dbAdapter!!.update(category!!) else dbAdapter!!.insert(category!!)
        dbAdapter!!.close()
        category = null
        editTextCategory!!.setText("")
        getCategory()
    }

    fun onDelete(view: View?) {
        if (category != null) {
            dbAdapter!!.open()
            if (dbAdapter!!.deleteCategory(category!!.id) > 0) {
                editTextCategory!!.setText("")
                category = null
            }
            dbAdapter!!.close()
            getCategory()
        } else {
            editTextCategory!!.setText("")
        }
    }

    fun onClose(view: View?) {
        val returnIntent = Intent()
        if (category != null) {
            returnIntent.putExtra(RESULT_CATEGORY, category!!.name)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    companion object {
        const val RESULT_CATEGORY = "RESULT_MONTH"
    }
}