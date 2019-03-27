package de.kontranik.freebudget.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.service.BackupAndRestore;
import de.kontranik.freebudget.service.FileService;

import static de.kontranik.freebudget.activity.OpenFileActivity.RESULT_FILENAME;

public class ToolsActivity extends AppCompatActivity {

    static final int RESULT_OPEN_FILENAME_REGULAR = 230;
    static final int RESULT_OPEN_FILENAME_NORMAL = 240;

    private static final int PERMISSION_REQUEST_CODE = 165;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        setTitle(R.string.tools);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!checkPermission())
            {
                requestPermission(); // Code for permission
            }
        }

        Button btn_ImportRegular = (Button) findViewById(R.id.btn_import_regular);
        Button btn_ExportRegular = (Button) findViewById(R.id.btn_export_regular);
        Button btn_ImportNormal = (Button) findViewById(R.id.btn_import_normal);
        Button btn_ExportNormal = (Button) findViewById(R.id.btn_export_normal);

        Button btn_backup = (Button) findViewById(R.id.btn_backup);
        Button btn_restore = (Button) findViewById(R.id.btn_restore);

        Button btn_close = (Button) findViewById(R.id.btn_close);

        btn_ImportRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importRegular(v);
            }
        });
        btn_ExportRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportRegular(v);
            }
        });
        btn_ImportNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importNormal(v);
            }
        });
        btn_ExportNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportNormal(v);
            }
        });
        btn_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup(v);
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreDialog(v);
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void importRegular(View view) {
        Intent open_import = new Intent(this, OpenFileActivity.class);
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_REGULAR);
    }

    private void exportRegular(View view) {
        try {
            String filename = "export_freebudget_regular_transaction";
            String result = FileService.exportFileRegular(filename, this);
            Toast.makeText(this, this.getResources().getString(R.string.exportOK_filename, result), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(this, this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void importNormal(View view) {
        Intent open_import = new Intent(this, OpenFileActivity.class);
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_NORMAL);
    }

    private void exportNormal(View view) {
        try {
            String filename = "export_freebudget_transaction";
            String result = FileService.exportFileTransaction(filename, this);
            Toast.makeText(this, this.getResources().getString(R.string.exportOK_filename, result), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(this, this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void backup(View view) {
        try {
            if ( BackupAndRestore.exportDB(view.getContext()) ) {
                Toast.makeText(this, this.getResources().getString(R.string.exportOK),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.exportFail),
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void restoreDialog(final View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                view.getContext());

        // set title
        alertDialogBuilder.setTitle(R.string.db_restore);

        // set dialog message
        alertDialogBuilder.setMessage(R.string.do_you_want_restore);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity
                restore(view);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void restore(View view) {
        try {
            if (BackupAndRestore.importDB(view.getContext())) {
                Toast.makeText(this, view.getResources().getString(R.string.importOK),
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, this.getResources().getString(R.string.importFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_OPEN_FILENAME_REGULAR:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);

                    try {
                        FileService.importFileRegular(fileName, this);
                        Toast.makeText(this,
                                this.getResources().getString(R.string.importOK_filename, fileName),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, this.getResources().getString(R.string.importFail, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case RESULT_OPEN_FILENAME_NORMAL:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);

                    try {
                        FileService.importFileTransaction(fileName, this);
                        Toast.makeText(this,
                                this.getResources().getString(R.string.importOK_filename, fileName),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, this.getResources().getString(R.string.importFail, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.request_write_permission, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(getResources().getString(R.string.app_name), "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e(getResources().getString(R.string.app_name), "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
