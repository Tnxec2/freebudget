package de.kontranik.freebudget.ui.components.settings

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import de.kontranik.freebudget.config.Config

class SettingsViewModelPreview: ViewModel(), ISettingsViewModel {
    override var sortOrderState = mutableIntStateOf(4)
    override var descOrderState = mutableStateOf(false)
    override var markLastEditedState = mutableStateOf(false)

    override fun changeSortOrderState(value: Int) {

    }

    override fun changeDescOrderState(value: Boolean) {

    }

    override fun changeMarkLastEditedState(value: Boolean) {

    }

    override fun saveConfig() {

    }
}