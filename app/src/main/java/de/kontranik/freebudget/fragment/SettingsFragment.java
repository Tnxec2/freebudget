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

public class SettingsFragment extends Fragment {

    SharedPreferences settings;
    RadioButton radioButton_Description, radioButton_CategoryName, radioButton_Amount, radioButton_Date, radioButton_EditDate,
            radioButton_notsort, radioButton_AbsAmount;
    CheckBox checkBox_Sortdesc, checkBox_MarkLastEdited;

    public SettingsFragment() {
        // Required empty public constructor
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

        radioButton_Description = (RadioButton) view.findViewById(R.id.radioButton_sort_description);
        radioButton_CategoryName = (RadioButton) view.findViewById(R.id.radioButton_sort_categoryname);
        radioButton_Amount = (RadioButton) view.findViewById(R.id.radioButton_sort_amount);
        radioButton_AbsAmount = (RadioButton) view.findViewById(R.id.radioButton_sort_absamount);
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

        String sort_order = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT);
        switch (sort_order) {
            case Config.PREF_ORDER_BY_DESCRIPTION:
                radioButton_Description.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_CATEGORY_NAME:
                radioButton_CategoryName.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_AMOUNT:
                radioButton_Amount.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_ABS_AMOUNT:
                radioButton_AbsAmount.setChecked(true);
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
                radioButton_Description.setChecked(true);
        }
        checkBox_Sortdesc.setChecked(settings.getBoolean(Config.PREF_SORT_DESC, false));
        checkBox_MarkLastEdited.setChecked(settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false));
    }

    public void saveConfig(View view) {
        String order_by = Config.PREF_ORDER_BY_NOT_SORT;
        // Einstellungen speichern
        if (radioButton_Description.isChecked()) {
            order_by = Config.PREF_ORDER_BY_DESCRIPTION;
        }
        if (radioButton_CategoryName.isChecked()) {
            order_by = Config.PREF_ORDER_BY_CATEGORY_NAME;
        }
        if (radioButton_Amount.isChecked()) {
            order_by = Config.PREF_ORDER_BY_AMOUNT;
        }
        if (radioButton_AbsAmount.isChecked()) {
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
