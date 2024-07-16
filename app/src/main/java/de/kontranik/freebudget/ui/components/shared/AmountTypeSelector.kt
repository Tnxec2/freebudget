package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun AmountTypeSelector(
    isIncome: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier) {

    val amountType = listOf(
        stringResource(id = R.string.activity_main_receipts),
        stringResource(id = R.string.activity_main_spending)
    )

    Column(Modifier.padding(start = 0.dp)) {
        amountType.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = ((text == amountType[0] && isIncome) || (text == amountType[1] && !isIncome)),
                        onClick = {
                            onValueChange(text == amountType[0])
                        }
                    )
                    .padding(start = 0.dp)
            ) {
                RadioButton(
                    selected = ((text == amountType[0] && isIncome) || (text == amountType[1] && !isIncome)),
                    onClick = {
                        onValueChange(text == amountType[0])
                    },
                    modifier = Modifier
                        .height(30.dp)
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                )
                Text(
                    text = text,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AmountTypeSelectorPreview() {
    Column {
        AmountTypeSelector(isIncome = true, onValueChange = {})
        AmountTypeSelector(isIncome = false, onValueChange = {})
    }
}