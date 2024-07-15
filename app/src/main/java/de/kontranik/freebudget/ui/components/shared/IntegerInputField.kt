package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun IntegerInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    decimalFormatter: DecimalFormatter = DecimalFormatter(false),
    label: String? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value.toString(),
        label = { if (label != null) Text(label) },
        onValueChange = {
            onValueChange(decimalFormatter.cleanup(it).toInt())
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}