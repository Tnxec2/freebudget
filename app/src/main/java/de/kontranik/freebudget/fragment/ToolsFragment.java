package de.kontranik.freebudget.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.activity.OpenFileActivity;
import de.kontranik.freebudget.service.FileService;

import static android.app.Activity.RESULT_OK;
import static de.kontranik.freebudget.activity.OpenFileActivity.RESULT_FILENAME;

public class ToolsFragment extends Fragment {

    static final int RESULT_OPEN_FILENAME_REGULAR = 230;
    static final int RESULT_OPEN_FILENAME_NORMAL = 240;

    private static final int PERMISSION_REQUEST_CODE = 165;

    private Button btn_ImportRegular, btn_ExportRegular, btn_ImportNormal, btn_ExportNormal;

    public ToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!checkPermission())
            {
                requestPermission(); // Code for permission
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tools, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        btn_ImportRegular = (Button) view.findViewById(R.id.btn_import_regular);
        btn_ExportRegular = (Button) view.findViewById(R.id.btn_export_regular);
        btn_ImportNormal = (Button) view.findViewById(R.id.btn_import_normal);
        btn_ExportNormal = (Button) view.findViewById(R.id.btn_export_normal);

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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void importRegular(View view) {
        Intent open_import = new Intent(getContext(), OpenFileActivity.class);
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_REGULAR);
    }

    private void exportRegular(View view) {
        try {
            String filename = "export_freebudget_regular_transaction";
            String result = FileService.exportFileRegular(filename, getContext());
            Toast.makeText(getContext(), this.getResources().getString(R.string.exportOK, result), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(getContext(), this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void importNormal(View view) {
        Intent open_import = new Intent(getContext(), OpenFileActivity.class);
        this.startActivityForResult(open_import, RESULT_OPEN_FILENAME_NORMAL);
    }

    private void exportNormal(View view) {
        try {
            String filename = "export_freebudget_transaction";
            String result = FileService.exportFileTransaction(filename, getContext());
            Toast.makeText(getContext(), this.getResources().getString(R.string.exportOK, result), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(getContext(), this.getResources().getString(R.string.exportFail, e.getLocalizedMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_OPEN_FILENAME_REGULAR:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);

                    Toast.makeText(getContext(),
                            this.getResources().getString(R.string.importFromFile, fileName),
                            Toast.LENGTH_SHORT).show();

                    try {
                        FileService.importFileRegular(fileName, getContext());
                        Toast.makeText(getContext(),
                                this.getResources().getString(R.string.importOK, fileName),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), this.getResources().getString(R.string.importFail, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case RESULT_OPEN_FILENAME_NORMAL:
                if (resultCode == RESULT_OK) {
                    String fileName = data.getStringExtra(RESULT_FILENAME);

                    Toast.makeText(getContext(),
                            this.getResources().getString(R.string.importFromFile, fileName),
                            Toast.LENGTH_SHORT).show();

                    try {
                        FileService.importFileTransaction(fileName, getContext());
                        Toast.makeText(getContext(),
                                this.getResources().getString(R.string.importOK),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), this.getResources().getString(R.string.importFail, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
