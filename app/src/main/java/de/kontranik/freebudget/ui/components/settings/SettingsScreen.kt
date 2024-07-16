package de.kontranik.freebudget.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    ) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.app_settings,
                drawerState = drawerState) }
    ) { padding ->
        Column(modifier
            .padding(padding)
            .fillMaxSize()
            .padding(paddingMedium)
            .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.app_settings_sort),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Column {
                Config.sortMap.values.forEach { sortType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = (sortType == settingsViewModel.sortOrderState.intValue),
                                onClick = {
                                    coroutineScope.launch {
                                        settingsViewModel.changeSortOrderState(sortType)
                                    }
                                }
                            )
                            .padding(start = paddingSmall)

                    ) {
                        RadioButton(
                            selected = (sortType == settingsViewModel.sortOrderState.intValue),
                            onClick = {
                                coroutineScope.launch {
                                    settingsViewModel.changeSortOrderState(sortType)
                                }
                            },
                            modifier = Modifier
                                .height(30.dp)
                                .padding(horizontal = 0.dp, vertical = 0.dp)
                        )
                        Text(
                            text = stringResource(id = sortType),
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                settingsViewModel.changeDescOrderState(settingsViewModel.descOrderState.value.not())
                            }
                        }
                    )
                    .fillMaxWidth()) {
                Checkbox(
                    checked = settingsViewModel.descOrderState.value,
                    onCheckedChange = {
                        coroutineScope.launch {
                            settingsViewModel.changeDescOrderState(settingsViewModel.descOrderState.value.not())
                        }
                    })
                Text(text = stringResource(id = R.string.app_settings_sort_desc))
            }

            Spacer(modifier = Modifier.height(paddingMedium))

            Text(
                text = stringResource(id = R.string.all_transactions),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                settingsViewModel.changeMarkLastEditedState(settingsViewModel.markLastEditedState.value.not())
                            }
                        }
                    )
                    .fillMaxWidth()) {
                Checkbox(
                    checked = settingsViewModel.markLastEditedState.value,
                    onCheckedChange = {
                        coroutineScope.launch {
                            settingsViewModel.changeMarkLastEditedState(settingsViewModel.markLastEditedState.value.not())
                        }
                    })
                Text(text = stringResource(id = R.string.mark_last_edited))
            }
        }
    }
}