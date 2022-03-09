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
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import de.kontranik.freebudget.activity.TransactionActivity
import java.io.File
import java.lang.Exception
import java.util.*

class OpenFileActivity : Activity(), View.OnClickListener, OnItemClickListener {
    var LvList: ListView? = null
    var textView_openFileName: TextView? = null
    var listItems = ArrayList<String>()
    var adapter: ArrayAdapter<String>? = null
    var BtnOK: Button? = null
    var BtnCancel: Button? = null
    var currentPath: String? = null
    var selectedFilePath: String? = null /* Full path, i.e. /mnt/sdcard/folder/file.txt */
    var selectedFileName: String? = null /* File Name Only, i.e file.txt */
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.menu_import_csv)
        setContentView(R.layout.activity_open_file)
        try {
            /* Initializing Widgets */
            LvList = findViewById(R.id.LvList)
            textView_openFileName = findViewById(R.id.textView_openFileName)
            BtnOK = findViewById(R.id.BtnOK)
            BtnCancel = findViewById(R.id.BtnCancel)

            /* Initializing Event Handlers */LvList.setOnItemClickListener(this)
            BtnOK.setOnClickListener(this)
            BtnCancel.setOnClickListener(this)

            //
            setCurrentPath(Environment.getExternalStorageDirectory().toString() + "/")
        } catch (ex: Exception) {
            Toast.makeText(
                this,
                "Error in OpenFileActivity.onCreate: " + ex.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setCurrentPath(path: String) {
        val folders = ArrayList<String>()
        val files = ArrayList<String>()
        currentPath = path
        val directory = File(path)
        val allEntries = directory.listFiles()
        for (entry in allEntries) {
            if (entry.isDirectory) {
                folders.add(entry.name)
            } else if (entry.isFile && entry.name.toLowerCase().endsWith("csv")) {
                files.add(entry.name)
            }
        }
        Collections.sort(folders) { s1, s2 -> s1.compareTo(s2, ignoreCase = true) }
        Collections.sort(files) { s1, s2 -> s1.compareTo(s2, ignoreCase = true) }
        listItems.clear()
        for (i in folders.indices) {
            listItems.add(folders[i] + "/")
        }
        listItems.addAll(files)
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listItems
        )
        adapter!!.notifyDataSetChanged()
        LvList!!.adapter = adapter
    }

    override fun onBackPressed() {
        if (currentPath != Environment.getExternalStorageDirectory().absolutePath + "/") {
            setCurrentPath(File(currentPath).parent + "/")
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.BtnOK -> {
                intent = Intent()
                intent.putExtra(RESULT_FILENAME, selectedFilePath)
                intent.putExtra(RESULT_SHORT_FILENAME, selectedFileName)
                setResult(RESULT_OK, intent)
                finish()
            }
            R.id.BtnCancel -> {
                intent = Intent()
                intent.putExtra(RESULT_FILENAME, "")
                intent.putExtra(RESULT_SHORT_FILENAME, "")
                setResult(RESULT_CANCELED, intent)
                finish()
            }
        }
    }

    override fun onItemClick(
        parent: AdapterView<*>, view: View, position: Int,
        id: Long
    ) {
        val entryName = parent.getItemAtPosition(position) as String
        if (entryName.endsWith("/")) {
            setCurrentPath(currentPath + entryName)
        } else {
            selectedFilePath = currentPath + entryName
            selectedFileName = entryName
            textView_openFileName!!.text = selectedFileName
            title = (this.resources.getString(R.string.title_activity_open_file)
                    + "[" + entryName + "]")
        }
    }

    companion object {
        const val RESULT_FILENAME = "filename"
        const val RESULT_SHORT_FILENAME = "short_filename"
    }
}