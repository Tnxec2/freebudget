package de.kontranik.freebudget.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.R
import de.kontranik.freebudget.databinding.ActivityOpenFileBinding
import java.io.File
import java.util.*

class OpenFileActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var binding: ActivityOpenFileBinding

    private var listItems = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private var currentPath: String? = null
    var selectedFilePath: String? = null /* Full path, i.e. /mnt/sdcard/folder/file.txt */
    var selectedFileName: String? = null /* File Name Only, i.e file.txt */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.menu_import_csv)
        binding = ActivityOpenFileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.LvList.onItemClickListener = this
        binding.BtnOK.setOnClickListener(this)
        binding.BtnCancel.setOnClickListener(this)

        try {
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
        allEntries?.forEach { entry ->
            if (entry.isDirectory) {
                folders.add(entry.name)
            } else if (entry.isFile && entry.name.lowercase(Locale.getDefault()).endsWith("csv")) {
                files.add(entry.name)
            }
        }
        folders.sortWith(Comparator { s1, s2 -> s1.compareTo(s2, ignoreCase = true) })
        files.sortWith(Comparator { s1, s2 -> s1.compareTo(s2, ignoreCase = true) })
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
        adapter.notifyDataSetChanged()
        binding.LvList.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentPath != null && currentPath != Environment.getExternalStorageDirectory().absolutePath + "/") {
            if (File(currentPath!!).parent != null) setCurrentPath(File(currentPath!!).parent!! + "/")
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
            binding.textViewOpenFileName.text = selectedFileName
            title = (this.resources.getString(R.string.title_activity_open_file)
                    + "[" + entryName + "]")
        }
    }

    companion object {
        const val RESULT_FILENAME = "filename"
        const val RESULT_SHORT_FILENAME = "short_filename"
    }
}