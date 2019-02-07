package de.kontranik.freebudget.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import de.kontranik.freebudget.R;
import de.kontranik.freebudget.config.Config;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    SharedPreferences settings;
    RadioButton radioButton_Name, radioButton_Cost, radioButton_Date, radioButton_EditDate,
            radioButton_notsort, radioButton_AbsCost;
    CheckBox checkBox_Sortdesc, checkBox_MarkLastEdited;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settings = this.getActivity().getSharedPreferences(Config.PREFS_FILE, MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        getActivity().setTitle("Settings");
        radioButton_Name = (RadioButton) view.findViewById(R.id.radioButton_sort_name);
        radioButton_Cost = (RadioButton) view.findViewById(R.id.radioButton_sort_cost);
        radioButton_AbsCost = (RadioButton) view.findViewById(R.id.radioButton_sort_abscost);
        radioButton_Date = (RadioButton) view.findViewById(R.id.radioButton_sort_date);
        radioButton_EditDate = (RadioButton) view.findViewById(R.id.radioButton_sort_edit_date);
        radioButton_notsort = (RadioButton) view.findViewById(R.id.radioButton_sort_notsort);
        checkBox_Sortdesc = (CheckBox) view.findViewById(R.id.checkBox_sort_desc);
        checkBox_MarkLastEdited = (CheckBox) view.findViewById(R.id.checkBox_markLastEdited);

        Button buttonSave = (Button) view.findViewById(R.id.button);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig(v);
            }
        });

        switch (settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)) {
            case Config.PREF_ORDER_BY_DESCRIPTION:
                radioButton_Name.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_AMOUNT:
                radioButton_Cost.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_ABS_AMOUNT:
                radioButton_AbsCost.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_DATE:
                radioButton_Date.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_EDIT_DATE:
                radioButton_EditDate.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_NOT_SORT:
                radioButton_notsort.setChecked(true);
                break;
            default:
                radioButton_Name.setChecked(true);
        }
        checkBox_Sortdesc.setChecked(settings.getBoolean(Config.PREF_SORT_DESC, false));
        checkBox_MarkLastEdited.setChecked(settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false));
    }

    public void saveConfig(View view) {
        String order_by = Config.PREF_ORDER_BY_NOT_SORT;
        // Einstellungen speichern
        if (radioButton_Name.isChecked()) {
            order_by = Config.PREF_ORDER_BY_DESCRIPTION;
        }
        if (radioButton_Cost.isChecked()) {
            order_by = Config.PREF_ORDER_BY_AMOUNT;
        }
        if (radioButton_AbsCost.isChecked()) {
            order_by = Config.PREF_ORDER_BY_ABS_AMOUNT;
        }
        if (radioButton_Date.isChecked()) {
            order_by = Config.PREF_ORDER_BY_DATE;
        }
        if (radioButton_EditDate.isChecked()) {
            order_by = Config.PREF_ORDER_BY_EDIT_DATE;
        }
        if (radioButton_notsort.isChecked()) {
            order_by = Config.PREF_ORDER_BY_NOT_SORT;
        }

        // сохраняем его в настройках
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(Config.PREF_ORDER_BY, order_by);
        prefEditor.putBoolean(Config.PREF_SORT_DESC, checkBox_Sortdesc.isChecked());
        prefEditor.putBoolean(Config.PREF_MARK_LAST_EDITED, checkBox_MarkLastEdited.isChecked());
        prefEditor.apply();
    }

}
