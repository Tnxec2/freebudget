package de.kontranik.freebudget.ui.components.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.config.Config


class SettingsViewModel(
    context: Context
) : ViewModel() {

    val settings = context.getSharedPreferences(Config.PREFS_FILE, Context.MODE_PRIVATE)
    var sortOrderState = mutableIntStateOf(0)
    var descOrderState = mutableStateOf(false)
    var markLastEditedState = mutableStateOf(false)

    init {
        sortOrderState.intValue =
                Config.getSortStringresourceID(
                    settings.getString(Config.PREF_ORDER_BY, Config.PREF_ORDER_BY_NOT_SORT)
                )
        descOrderState.value = settings.getBoolean(Config.PREF_SORT_DESC, false)

        markLastEditedState.value = settings.getBoolean(Config.PREF_MARK_LAST_EDITED, false)
    }

    fun changeSortOrderState(value: Int) {
        sortOrderState.intValue = value
        saveConfig()
    }

    fun changeDescOrderState(value: Boolean) {
        descOrderState.value = value
        saveConfig()
    }

    fun changeMarkLastEditedState(value: Boolean) {
        markLastEditedState.value = value
        saveConfig()
    }


    private fun saveConfig() {
        val prefEditor: SharedPreferences.Editor = settings.edit()
        prefEditor.putString(Config.PREF_ORDER_BY, Config.getSortStringForId(sortOrderState.intValue))
        prefEditor.putBoolean(Config.PREF_SORT_DESC, descOrderState.value)
        prefEditor.putBoolean(Config.PREF_MARK_LAST_EDITED, markLastEditedState.value)
        prefEditor.apply()
    }
}