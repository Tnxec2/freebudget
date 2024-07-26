package de.kontranik.freebudget.ui.components.settings.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingMedium

@Composable
fun SettingsCard(
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(
            paddingMedium
        )) {
            if (title != null) SettingsTitle(
                text = title
            )
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsCardPreview() {
    AppTheme {

        SettingsCard(
            title = "Settings"
        ) {
            SettingsCheckbox(
                value = true,
                label = "Checkbox",
                onChange = {}
            )
            SettingsTextField(
                value = "Value 1",
                label = "Entry 1",
                onChange = {}
            )
        }

    }
}
