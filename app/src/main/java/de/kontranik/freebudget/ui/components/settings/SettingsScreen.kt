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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.config.Config
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.settings.elements.SettingsCard
import de.kontranik.freebudget.ui.components.settings.elements.SettingsCheckbox
import de.kontranik.freebudget.ui.components.settings.elements.SettingsRadioButton
import de.kontranik.freebudget.ui.components.shared.PreviewLandscapeLight
import de.kontranik.freebudget.ui.components.shared.PreviewPortraitLightDark
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    settingsViewModel: ISettingsViewModel,
    ) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.title_settings,
                drawerState = drawerState) }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .fillMaxSize()
                .padding(paddingMedium)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCard(
                title = stringResource(id = R.string.app_settings_sort)
            ) {

                Column {
                    Config.sortMap.values.forEach { sortType ->
                        SettingsRadioButton(
                            selected = (sortType == settingsViewModel.sortOrderState.intValue),
                            label = stringResource(id = sortType),
                            onClick = {
                                coroutineScope.launch {
                                    settingsViewModel.changeSortOrderState(sortType)
                                }
                        })
                    }
                }

                SettingsCheckbox(value = settingsViewModel.descOrderState.value,
                    label = stringResource(id = R.string.app_settings_sort_desc),
                    onChange = {
                        coroutineScope.launch {
                            settingsViewModel.changeMarkLastEditedState(settingsViewModel.descOrderState.value.not())
                        }
                    }
                )

            }

            Spacer(modifier = Modifier.height(paddingMedium))

            SettingsCard(
                title = stringResource(id = R.string.title_all_transactions),
                modifier = Modifier
            ) {
                SettingsCheckbox(value = settingsViewModel.markLastEditedState.value,
                    label = stringResource(id = R.string.mark_last_edited),
                    onChange = {
                        coroutineScope.launch {
                            settingsViewModel.changeMarkLastEditedState(settingsViewModel.markLastEditedState.value.not())
                        }
                    }
                )
            }
        }
    }
}

@PreviewPortraitLightDark
@Composable
private fun SettingsScreenPreview() {
    val viewModel = SettingsViewModelPreview()

    AppTheme {
        SettingsScreen(
            drawerState = DrawerState(DrawerValue.Closed),
            settingsViewModel = viewModel
        )
    }
}