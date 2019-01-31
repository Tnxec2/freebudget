package de.kontranik.freebudget.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.kontranik.freebudget.R;

public class OpenFileActivity extends Activity
        implements OnClickListener, OnItemClickListener {

    ListView LvList;
    TextView textView_openFileName;
    ArrayList<String> listItems = new ArrayList<>();

    ArrayAdapter<String> adapter;

    Button BtnOK;
    Button BtnCancel;

    String currentPath = null;

    String selectedFilePath = null; /* Full path, i.e. /mnt/sdcard/folder/file.txt */
    String selectedFileName = null; /* File Name Only, i.e file.txt */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle(R.string.menu_import_csv);
        setContentView(R.layout.activity_open_file);

        try {
            /* Initializing Widgets */
            LvList = findViewById(R.id.LvList);
            textView_openFileName = findViewById(R.id.textView_openFileName);
            BtnOK = findViewById(R.id.BtnOK);
            BtnCancel = findViewById(R.id.BtnCancel);

            /* Initializing Event Handlers */

            LvList.setOnItemClickListener(this);

            BtnOK.setOnClickListener(this);
            BtnCancel.setOnClickListener(this);

            //
            setCurrentPath(Environment.getExternalStorageDirectory().toString() + "/");
        } catch (Exception ex) {
            Toast.makeText(this,
                    "Error in OpenFileActivity.onCreate: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    void setCurrentPath(String path) {
        ArrayList<String> folders = new ArrayList<>();

        ArrayList<String> files = new ArrayList<>();

        currentPath = path;

        File directory = new File(path);
        File[] allEntries = directory.listFiles();

        for (File allEntry : allEntries) {
            if (allEntry.isDirectory()) {
                folders.add(allEntry.getName());
            } else if (allEntry.isFile()) {
                files.add(allEntry.getName());
            }
        }

        Collections.sort(folders, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        listItems.clear();

        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        listItems.addAll(files);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        adapter.notifyDataSetChanged();

        LvList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        if (!currentPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath() + "/")) {
            setCurrentPath(new File(currentPath).getParent() + "/");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.BtnOK:

                intent = new Intent();
                intent.putExtra("fileName", selectedFilePath);
                intent.putExtra("shortFileName", selectedFileName);
                setResult(RESULT_OK, intent);

                this.finish();

                break;
            case R.id.BtnCancel:

                intent = new Intent();
                intent.putExtra("fileName", "");
                intent.putExtra("shortFileName", "");
                setResult(RESULT_CANCELED, intent);

                this.finish();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String entryName = (String)parent.getItemAtPosition(position);
        if (entryName.endsWith("/")) {
            setCurrentPath(currentPath + entryName);
        } else {
            selectedFilePath = currentPath + entryName;

            selectedFileName = entryName;

            textView_openFileName.setText(selectedFileName);
            setTitle(this.getResources().getString(R.string.title_activity_open_file)
                    + "[" + entryName + "]");
        }
    }
}
