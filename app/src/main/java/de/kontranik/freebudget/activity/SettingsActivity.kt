package de.kontranik.freebudget.activity

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import de.kontranik.freebudget.config.Config

class SettingsActivity : AppCompatActivity() {
    var settings: SharedPreferences? = null
    var radioButton_Description: RadioButton? = null
    var radioButton_CategoryName: RadioButton? = null
    var radioButton_Amount: RadioButton? = null
    var radioButton_Date: RadioButton? = null
    var radioButton_EditDate: RadioButton? = null
    var radioButton_notsort: RadioButton? = null
    var radioButton_AbsAmount: RadioButton? = null
    var checkBox_Sortdesc: CheckBox? = null
    var checkBox_MarkLastEdited: CheckBox? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        settings = getSharedPreferences(Config.PREFS_FILE, MODE_PRIVATE)
        setTitle(R.string.app_settings)
        radioButton_Description = findViewById(R.id.radioButton_sort_description) as RadioButton?
        radioButton_CategoryName = findViewById(R.id.radioButton_sort_categoryname) as RadioButton?
        radioButton_Amount = findViewById(R.id.radioButton_sort_amount) as RadioButton?
        radioButton_AbsAmount = findViewById(R.id.radioButton_sort_absamount) as RadioButton?
        radioButton_Date = findViewById(R.id.radioButton_sort_date) as RadioButton?
        radioButton_EditDate = findViewById(R.id.radioButton_sort_edit_date) as RadioButton?
        radioButton_notsort = findViewById(R.id.radioButton_sort_notsort) as RadioButton?
        checkBox_Sortdesc = findViewById(R.id.checkBox_sort_desc) as CheckBox?
        checkBox_MarkLastEdited = findViewById(R.id.checkBox_markLastEdited) as CheckBox?
        val btn_close = findViewById(R.id.btn_close) as Button
        btn_close.setOnClickListener { finish() }
        val buttonSave = findViewById(R.id.button) as Button
        buttonSave.setOnClickListener { v -> saveConfig(v) }
        val sort_order: String =
            settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
        when (sort_order) {
            Config.PREF_ORDER_BY_DESCRIPTION -> radioButton_Description.setChecked(true)
            Config.PREF_ORDER_BY_CATEGORY_NAME -> radioButton_CategoryName.setChecked(true)
            Config.PREF_ORDER_BY_AMOUNT -> radioButton_Amount.setChecked(true)
            Config.PREF_ORDER_BY_ABS_AMOUNT -> radioButton_AbsAmount.setChecked(true)
            Config.PREF_ORDER_BY_DATE -> radioButton_Date.setChecked(true)
            Config.PREF_ORDER_BY_EDIT_DATE -> radioButton_EditDate.setChecked(true)
            Config.PREF_ORDER_BY_NOT_SORT -> radioButton_notsort.setChecked(true)
            else -> radioButton_Description.setChecked(true)
        }
        checkBox_Sortdesc.setChecked(settings.getBoolean(Config.PREF_SORT_DESC, false))
        checkBox_MarkLastEdited.setChecked(settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false))
    }

    fun saveConfig(view: View?) {
        var order_by = Config.PREF_ORDER_BY_NOT_SORT
        // Einstellungen speichern
        if (radioButton_Description.isChecked()) {
            order_by = Config.PREF_ORDER_BY_DESCRIPTION
        }
        if (radioButton_CategoryName.isChecked()) {
            order_by = Config.PREF_ORDER_BY_CATEGORY_NAME
        }
        if (radioButton_Amount.isChecked()) {
            order_by = Config.PREF_ORDER_BY_AMOUNT
        }
        if (radioButton_AbsAmount.isChecked()) {
            order_by = Config.PREF_ORDER_BY_ABS_AMOUNT
        }
        if (radioButton_Date.isChecked()) {
            order_by = Config.PREF_ORDER_BY_DATE
        }
        if (radioButton_EditDate.isChecked()) {
            order_by = Config.PREF_ORDER_BY_EDIT_DATE
        }
        if (radioButton_notsort.isChecked()) {
            order_by = Config.PREF_ORDER_BY_NOT_SORT
        }

        // сохраняем его в настройках
        val prefEditor: SharedPreferences.Editor = settings.edit()
        prefEditor.putString(Config.PREF_ORDER_BY, order_by)
        prefEditor.putBoolean(Config.PREF_SORT_DESC, checkBox_Sortdesc.isChecked())
        prefEditor.putBoolean(Config.PREF_MARK_LAST_EDITED, checkBox_MarkLastEdited.isChecked())
        prefEditor.apply()
        finish()
    }
}