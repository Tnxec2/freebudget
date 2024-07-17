package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.helpers.DateUtils
import de.kontranik.freebudget.ui.theme.paddingSmall


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(
    millis: Long?,
    onChangeDate: (millis: Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    val state: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = millis ?: DateUtils.now(),
        initialDisplayMode = DisplayMode.Picker,
    )

    LaunchedEffect(key1 = millis) {
        state.selectedDateMillis = millis ?: DateUtils.now()
    }

    var open by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier
        .padding(start = paddingSmall)
        .fillMaxWidth()) {

    Button(
        onClick = { open = true },
        modifier = Modifier
            .padding(start = paddingSmall)
            .fillMaxWidth()
    ) {
        Text(text = if (millis != null) DateUtils.getDateMedium(millis) else stringResource(id = R.string.not_setted))
    }
    Box(modifier = Modifier) {
        if (open) {
            DatePickerDialog(
                onDismissRequest = { open = false },
                confirmButton = {
                    Button(
                        onClick = {
                            onChangeDate(state.selectedDateMillis!!)
                            open = false
                        }
                    ) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { open = false }
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                }
            ) {
                DatePicker(
                    state = state,
                    showModeToggle = true
                )
            }
        }
    }

    }
}