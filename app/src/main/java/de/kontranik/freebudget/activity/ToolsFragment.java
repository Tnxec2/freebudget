package de.kontranik.freebudget.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.service.FileService;

import static android.app.Activity.RESULT_OK;
import static de.kontranik.freebudget.activity.OpenFileActivity.RESULT_FILENAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToolsFragment} interface
 * to handle interaction events.
 * Use the {@link ToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolsFragment extends Fragment {

    static final int RESULT_OPEN_FILENAME_REGULAR = 230;
    static final int RESULT_OPEN_FILENAME_NORMAL = 240;

    private Button btn_ImportRegular, btn_ExportRegular, btn_ImportNormal, btn_ExportNormal;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolsFragment newInstance(String param1, String param2) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        getActivity().setTitle("Tools");

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
            FileService.exportFileRegular("export_freebudget_regular_transaction", getContext());
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
            FileService.exportFileTransaction("export_freebudget_transaction", getContext());
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
                                this.getResources().getString(R.string.importOK),
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
}
