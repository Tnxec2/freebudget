package de.kontranik.freebudget.ui.components.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.config.Config


class SettingsViewModel(
    context: Context
) : ViewModel(), ISettingsViewModel {

    private val settings: SharedPreferences = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
    override var sortOrderState = mutableIntStateOf(0)
    override var descOrderState = mutableStateOf(false)
    override var markLastEditedState = mutableStateOf(false)

    init {
        sortOrderState.intValue =
                Config.getSortStringresourceID(
                    settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
                )
        descOrderState.value = settings.getBoolean(Config.PREF_SORT_DESC, false)

        markLastEditedState.value = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }

    override fun changeSortOrderState(value: Int) {
        sortOrderState.intValue = value
        saveConfig()
    }

    override fun changeDescOrderState(value: Boolean) {
        descOrderState.value = value
        saveConfig()
    }

    override fun changeMarkLastEditedState(value: Boolean) {
        markLastEditedState.value = value
        saveConfig()
    }


    override fun saveConfig() {
        val prefEditor: SharedPreferences.Editor = settings.edit()
        prefEditor.putString(Config.PREF_ORDER_BY, Config.getSortStringForId(sortOrderState.intValue))
        prefEditor.putBoolean(Config.PREF_SORT_DESC, descOrderState.value)
        prefEditor.putBoolean(Config.PREF_MARK_LAST_EDITED, markLastEditedState.value)
        prefEditor.apply()
    }
}