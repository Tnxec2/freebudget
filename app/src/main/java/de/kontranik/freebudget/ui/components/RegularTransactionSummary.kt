package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun RegularTransactionSummary(
    income: Double,
    bills: Double,
    modifier: Modifier = Modifier) {
    Column(Modifier.padding(paddingSmall)) {
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_receipts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = modifier
                    .weight(1f)
            )
            Amount(amount = income, modifier)
        }
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_spending),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = modifier
                    .weight(1f)
            )
            Amount(amount = bills, modifier)
        }
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_total),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = modifier
                    .weight(1f)
            )
            Amount(amount = income - bills, modifier)
        }
    }
}
