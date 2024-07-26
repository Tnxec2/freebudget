package de.kontranik.freebudget.ui.components.settings.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@Composable
fun SettingsRadioButton(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = {
                    onClick()
                }
            )

    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = {
                onClick()
            },
            modifier = Modifier
                .height(30.dp)
                .padding(horizontal = 0.dp, vertical = 0.dp)
        )
    }
}

@Preview
@Composable
private fun SettingsCheckboxPreview() {
    SettingsRadioButton(
        selected = true,
        label = "RadioButton",
        onClick = {}
    )
}