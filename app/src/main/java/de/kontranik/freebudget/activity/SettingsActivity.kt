package de.kontranik.freebudget.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.kontranik.freebudget.R
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = getSharedPreferences(Config.PREFS_FILE, MODE_PRIVATE)
        setTitle(R.string.app_settings)

        binding.btnClose.setOnClickListener { finish() }
        binding.buttonSave.setOnClickListener { saveConfig() }

        val sortOrder = settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
        when (sortOrder) {
            Config.PREF_ORDER_BY_DESCRIPTION -> binding.radioButtonSortDescription.isChecked = true
            Config.PREF_ORDER_BY_CATEGORY_NAME -> binding.radioButtonSortCategoryname.isChecked = true
            Config.PREF_ORDER_BY_AMOUNT -> binding.radioButtonSortAmount.isChecked = true
            Config.PREF_ORDER_BY_ABS_AMOUNT -> binding.radioButtonSortAbsamount.isChecked = true
            Config.PREF_ORDER_BY_DATE -> binding.radioButtonSortDate.isChecked = true
            Config.PREF_ORDER_BY_EDIT_DATE -> binding.radioButtonSortEditDate.isChecked = true
            Config.PREF_ORDER_BY_NOT_SORT -> binding.radioButtonSortNotsort.isChecked = true
            else -> binding.radioButtonSortDescription.isChecked = true
        }
        binding.checkBoxSortDesc.isChecked = settings.getBoolean(Config.PREF_SORT_DESC, false)
        binding.checkBoxMarkLastEdited.isChecked =
            settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }

    private fun saveConfig() {
        var orderBy = Config.PREF_ORDER_BY_NOT_SORT

        if (binding.radioButtonSortDescription.isChecked) {
            orderBy = Config.PREF_ORDER_BY_DESCRIPTION
        }
        if (binding.radioButtonSortCategoryname.isChecked) {
            orderBy = Config.PREF_ORDER_BY_CATEGORY_NAME
        }
        if (binding.radioButtonSortAmount.isChecked) {
            orderBy = Config.PREF_ORDER_BY_AMOUNT
        }
        if (binding.radioButtonSortAbsamount.isChecked) {
            orderBy = Config.PREF_ORDER_BY_ABS_AMOUNT
        }
        if (binding.radioButtonSortDate.isChecked) {
            orderBy = Config.PREF_ORDER_BY_DATE
        }
        if (binding.radioButtonSortEditDate.isChecked) {
            orderBy = Config.PREF_ORDER_BY_EDIT_DATE
        }
        if (binding.radioButtonSortNotsort.isChecked) {
            orderBy = Config.PREF_ORDER_BY_NOT_SORT
        }

        val prefEditor: SharedPreferences.Editor = settings.edit()
        prefEditor.putString(Config.PREF_ORDER_BY, orderBy)
        prefEditor.putBoolean(Config.PREF_SORT_DESC, binding.checkBoxSortDesc.isChecked)
        prefEditor.putBoolean(Config.PREF_MARK_LAST_EDITED, binding.checkBoxMarkLastEdited.isChecked)
        prefEditor.apply()

        finish()
    }
}