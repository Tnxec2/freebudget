package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.text.DateFormat
import java.util.Locale


@Composable
fun RegularTransactionItem(
    regularTransaction: RegularTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {

    var text = stringResource(
        R.string.subTitleTransaction,
        java.lang.String.valueOf(regularTransaction.day),
        regularTransaction.category
    )
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    if (!regularTransaction.isDateNull(regularTransaction.dateStart)) text += ", " + stringResource(
        R.string.start) + ": " + df.format(regularTransaction.dateStart)
    if (!regularTransaction.isDateNull(regularTransaction.dateEnd)) text += ", " + stringResource(
        R.string.end) + ": " + df.format(regularTransaction.dateEnd)

    Column(
        Modifier
            .clickable { onClick() }
            .padding(horizontal = paddingSmall)
    ) {
        Row(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = regularTransaction.description,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = modifier
                    .weight(1f)
            )
            Amount(amount = regularTransaction.amount, modifier)
        }  
        Text(
            text = text,
            modifier
                .fillMaxWidth()
        )
    } 
}


@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun RegularTransactionItemPreview() {
    RegularTransactionItem(
        regularTransaction = RegularTransaction(0, 1, "Test", "Category", 125.00, null  ),
        onClick = { })
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun RegularTransactionItemPreviewMinus() {
    RegularTransactionItem(
        regularTransaction = RegularTransaction(1, 15, "Test2", "Category", -125.00, null  ),
        onClick = { })
}
