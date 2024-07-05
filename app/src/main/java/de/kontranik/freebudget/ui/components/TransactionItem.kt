package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.theme.Typography
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.text.DateFormat
import java.util.Locale


@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    marked: Boolean,
    modifier: Modifier = Modifier) {

    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    val dateString = if (transaction.date > 0) df.format(transaction.date) else stringResource(R.string.not_setted)

    Column(
        modifier
            .background(color = if (marked) colorResource(R.color.colorBackgroundAccent) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = paddingSmall)
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

