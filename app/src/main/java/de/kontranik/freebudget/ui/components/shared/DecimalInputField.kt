package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun DecimalInputField(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    decimalFormatter: DecimalFormatter = DecimalFormatter(true),
    label: String? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        label = { if (label != null) Text(label) },
        value = value.toString(),
        onValueChange = {
            onValueChange(decimalFormatter.cleanup(it).toDouble())
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}