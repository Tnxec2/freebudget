package de.kontranik.freebudget.activity

import android.Manifest
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import de.kontranik.freebudget.activity.TransactionActivity
import java.io.IOException
import java.lang.Exception

class ToolsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        setTitle(R.string.tools)
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission() // Code for permission
            }
        }
        val btn_ImportRegular = findViewById<View>(R.id.btn_import_regular) as Button
        val btn_ExportRegular = findViewById<View>(R.id.btn_export_regular) as Button
        val btn_ImportNormal = findViewById<View>(R.id.btn_import_normal) as Button
        val btn_ExportNormal = findViewById<View>(R.id.btn_export_normal) as Button
        val btn_backup = findViewById<View>(R.id.btn_backup) as Button
        val btn_restore = findViewById<View>(R.id.btn_restore) as Button
        val btn_close = findViewById<View>(R.id.btn_close) as Button
        btn_ImportRegular.setOnClickListener { v -> importRegular(v) }
        btn_ExportRegular.setOnClickListener { v -> exportRegular(v) }
        btn_ImportNormal.setOnClickListener { v -> importNormal(v) }
        btn_ExportNormal.setOnClickListener { v -> exportNormal(v) }
        btn_backup.setOnClickListener { v -> backup(v) }
        btn_restore.setOnClickListener { v -> restoreDialog(v) }
        btn_close.setOnClickListener { finish() }
    }

    private fun importRegular(view: View) {
        val open_import = Intent(this, OpenFileActivity::class.java)
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_REGULAR)
    }

    private fun exportRegular(view: View) {
        try {
            val filename = "export_freebudget_regular_transaction"
            val result = exportFileRegular(filename, this)
            Toast.makeText(
                this,
                this.resources.getString(R.string.exportOK_filename, result),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: IOException) {
            //e.printStackTrace();
            Toast.makeText(
                this, this.resources.getString(R.string.exportFail, e.localizedMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun importNormal(view: View) {
        val open_import = Intent(this, OpenFileActivity::class.java)
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_NORMAL)
    }

    private fun exportNormal(view: View) {
        try {
            val filename = "export_freebudget_transaction"
            val result = exportFileTransaction(filename, this)
            Toast.makeText(
                this,
                this.resources.getString(R.string.exportOK_filename, result),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: IOException) {
            //e.printStackTrace();
            Toast.makeText(
                this, this.resources.getString(R.string.exportFail, e.localizedMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun backup(view: View) {
        try {
            if (exportDB(view.context)) {
                Toast.makeText(
                    this, this.resources.getString(R.string.exportOK),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, this.resources.getString(R.string.exportFail),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: IOException) {
            Toast.makeText(
                this, this.resources.getString(R.string.exportFail, e.localizedMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun restoreDialog(view: View) {
        val alertDialogBuilder = AlertDialog.Builder(
            view.context
        )

        // set title
        alertDialogBuilder.setTitle(R.string.db_restore)

        // set dialog message
        alertDialogBuilder.setMessage(R.string.do_you_want_restore)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(R.string.yes) { dialog, id -> // if this button is clicked, close
            // current activity
            restore(view)
        }
        alertDialogBuilder.setNegativeButton(R.string.no) { dialog, id -> // if this button is clicked, just close
            // the dialog box and do nothing
            dialog.cancel()
        }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    private fun restore(view: View) {
        try {
            if (importDB(view.context)) {
                Toast.makeText(
                    this, view.resources.getString(R.string.importOK),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: IOException) {
            Toast.makeText(
                this, this.resources.getString(R.string.importFail, e.localizedMessage),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_OPEN_FILENAME_REGULAR -> if (resultCode == RESULT_OK) {
                val fileName = data!!.getStringExtra(OpenFileActivity.Companion.RESULT_FILENAME)
                try {
                    importFileRegular(fileName, this)
                    Toast.makeText(
                        this,
                        this.resources.getString(R.string.importOK_filename, fileName),
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this, this.resources.getString(R.string.importFail, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            RESULT_OPEN_FILENAME_NORMAL -> if (resultCode == RESULT_OK) {
                val fileName = data!!.getStringExtra(OpenFileActivity.Companion.RESULT_FILENAME)
                try {
                    importFileTransaction(fileName, this)
                    Toast.makeText(
                        this,
                        this.resources.getString(R.string.importOK_filename, fileName),
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this, this.resources.getString(R.string.importFail, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(this, R.string.request_write_permission, Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(
                    resources.getString(R.string.app_name),
                    "Permission Granted, Now you can use local drive ."
                )
            } else {
                Log.e(
                    resources.getString(R.string.app_name),
                    "Permission Denied, You cannot use local drive ."
                )
            }
        }
    }

    companion object {
        const val RESULT_OPEN_FILENAME_REGULAR = 230
        const val RESULT_OPEN_FILENAME_NORMAL = 240
        private const val PERMISSION_REQUEST_CODE = 165
    }
}