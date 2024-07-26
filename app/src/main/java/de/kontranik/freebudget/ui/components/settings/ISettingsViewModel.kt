package de.kontranik.freebudget.ui.components.settings

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

interface ISettingsViewModel {
    var sortOrderState: MutableIntState
    var descOrderState: MutableState<Boolean>
    var markLastEditedState: MutableState<Boolean>
    fun changeSortOrderState(value: Int)
    fun changeDescOrderState(value: Boolean)
    fun changeMarkLastEditedState(value: Boolean)
    fun saveConfig()
}