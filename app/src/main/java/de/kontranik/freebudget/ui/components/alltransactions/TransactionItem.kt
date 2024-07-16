package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.helpers.DateUtils
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Calendar


@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    marked: Boolean,
    modifier: Modifier = Modifier) {

    val dateString = if (transaction.date > 0) DateUtils.getDateMedium(transaction.date) else stringResource(R.string.not_setted)

    Column(
        Modifier
            .background(color = if (marked) MaterialTheme.colorScheme.inversePrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = paddingSmall)
    ) {
        Row(
            modifier.fillMaxWidth()
        ) {
            Text(
                text = transaction.description,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = modifier
                    .weight(1f)
            )
            Amount(amount = transaction.amountPlanned, modifier)
            Amount(amount = transaction.amountFact, modifier)
        }  
        Text(
            text = stringResource(id = R.string.subTitleTransaction, dateString, transaction.category),
            modifier = modifier
                        .fillMaxWidth()
        )
    } 
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionItemPreview() {
    TransactionItem(
        transaction = Transaction(null, null, "Test", "Category",
            DateUtils.now(), 10.25, 58.23, DateUtils.now(), 0L, null ),
        onClick = { }, marked = false)
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSItemPreviewMinus() {
    TransactionItem(
        transaction = Transaction(null, null, "Test", "Category",
            DateUtils.now(), -58.23, -44.20, DateUtils.now(), 0L, null ),
        onClick = { }, marked = false)
}