package de.kontranik.freebudget.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.kontranik.freebudget.R
import de.kontranik.freebudget.databinding.ActivityToolsBinding
import de.kontranik.freebudget.service.BackupAndRestore.exportDB
import de.kontranik.freebudget.service.BackupAndRestore.importDB
import de.kontranik.freebudget.service.FileService.exportFileRegular
import de.kontranik.freebudget.service.FileService.exportFileTransaction
import de.kontranik.freebudget.service.FileService.importFileRegular
import de.kontranik.freebudget.service.FileService.importFileTransaction
import java.io.IOException

class ToolsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToolsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.tools)
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission() // Code for permission
            }
        }

        binding.btnImportRegular.setOnClickListener { importRegular() }
        binding.btnExportRegular.setOnClickListener { exportRegular() }
        binding.btnImportNormal.setOnClickListener { importNormal() }
        binding.btnExportNormal.setOnClickListener { exportNormal() }
        binding.btnBackup.setOnClickListener { v -> backup() }
        binding.btnRestore.setOnClickListener { v -> restoreDialog() }
        binding.btnClose.setOnClickListener { finish() }
    }

    private fun importRegular() {
        val openImport = Intent(this, OpenFileActivity::class.java)
        this.startActivityForResult(openImport, RESULT_OPEN_FILENAME_REGULAR)
    }

    private fun exportRegular() {
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

    private fun importNormal() {
        val openImport = Intent(this, OpenFileActivity::class.java)
        this.startActivityForResult(openImport, RESULT_OPEN_FILENAME_NORMAL)
    }

    private fun exportNormal() {
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

    private fun backup() {
        try {
            if (exportDB(this)) {
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

    private fun restoreDialog() {
        val alertDialogBuilder = AlertDialog.Builder(
            this
        )

        // set title
        alertDialogBuilder.setTitle(R.string.db_restore)

        // set dialog message
        alertDialogBuilder.setMessage(R.string.do_you_want_restore)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(R.string.yes) { dialog, id -> // if this button is clicked, close
            // current activity
            restore()
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

    private fun restore() {
        try {
            if (importDB(this)) {
                Toast.makeText(
                    this, resources.getString(R.string.importOK),
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