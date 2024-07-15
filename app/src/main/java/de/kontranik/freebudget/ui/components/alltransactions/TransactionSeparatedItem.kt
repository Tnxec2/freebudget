package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun TransactionSeparatedItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {

    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    val dateString = if (transaction.date > 0) df.format(transaction.date) else stringResource(R.string.not_setted)

    Column(
        Modifier
            .clickable { onClick() }
            .padding(horizontal = paddingSmall)
    ) {
        Row(
            modifier.fillMaxWidth()
        ) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier
                    .weight(1f)
            )
            Amount(
                amount = if (transaction.amountFact != 0.0) transaction.amountFact else transaction.amountPlanned, modifier)
        }  
        Text(
            text = stringResource(id = R.string.subTitleTransaction, dateString, transaction.category),
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                        .fillMaxWidth()
        )
    } 
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSepartedItemPreview() {
    TransactionSeparatedItem(
        transaction = Transaction(null, null, "Test", "Category",
            Calendar.getInstance().timeInMillis, 0.0, 58.23, Calendar.getInstance().timeInMillis, 0L, null ),
        onClick = { })
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSepartedItemPreviewMinus() {
    TransactionSeparatedItem(
        transaction = Transaction(null, null, "Test", "Category",
            Calendar.getInstance().timeInMillis, -58.23, 0.0, Calendar.getInstance().timeInMillis, 0L, null ),
        onClick = { })
}