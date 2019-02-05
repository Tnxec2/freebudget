package de.kontranik.freebudget.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import de.kontranik.freebudget.config.Config;
import de.kontranik.freebudget.R;

public class ConfigActivity extends AppCompatActivity {
    SharedPreferences settings;
    RadioButton radioButton_Name, radioButton_Cost, radioButton_EditDate,
                radioButton_notsort, radioButton_AbsCost;
    CheckBox checkBox_Sortdesc;
    String order_by;
    Boolean sort_desc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_settings);
        setContentView(R.layout.activity_config);
        settings = getSharedPreferences(Config.PREFS_FILE, MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // gespeicherte Einstellungen holen
        radioButton_Name = findViewById(R.id.radioButton_sort_name);
        radioButton_Cost = findViewById(R.id.radioButton_sort_cost);
        radioButton_AbsCost = findViewById(R.id.radioButton_sort_abscost);
        radioButton_EditDate = findViewById(R.id.radioButton_sort_edit_date);
        radioButton_notsort = findViewById(R.id.radioButton_sort_notsort);
        checkBox_Sortdesc = findViewById(R.id.checkBox_sort_desc);

        order_by = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT);
        sort_desc = settings.getBoolean(Config.PREF_SORT_DESC, false);
        switch (order_by) {
            case Config.PREF_ORDER_BY_DESCRIPTION:
                radioButton_Name.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_AMOUNT:
                radioButton_Cost.setChecked(true);
                break;
            case Config.PREF_ORDER_BY_ABS_AMOUNT:
                radioButton_AbsCost.setChecked(true);
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
        checkBox_Sortdesc.setChecked(sort_desc);
    }

    public void saveConfig(View view) {
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
        if (radioButton_EditDate.isChecked()) {
            order_by = Config.PREF_ORDER_BY_EDIT_DATE;
        }
        if (radioButton_notsort.isChecked()) {
            order_by = Config.PREF_ORDER_BY_NOT_SORT;
        }
        sort_desc = checkBox_Sortdesc.isChecked();

        // сохраняем его в настройках
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(Config.PREF_ORDER_BY, order_by);
        prefEditor.putBoolean(Config.PREF_SORT_DESC, sort_desc);
        prefEditor.apply();
        this.finish();
    }
}
