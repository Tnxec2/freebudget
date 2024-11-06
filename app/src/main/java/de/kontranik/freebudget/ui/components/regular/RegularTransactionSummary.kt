package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.components.shared.swipableModifier
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun RegularTransactionSummary(
    income: Double,
    bills: Double,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,) {
    Column(
        swipableModifier(
        modifier = modifier,
        onLeft = onPrev,
        onRight = onNext).padding(paddingSmall)) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth(1f)
            )
        }
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_receipts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
            )
            Amount(amount = income, modifier)
        }
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_spending),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
            )
            Amount(amount = bills, Modifier)
        }
        Row {
            Text(
                text = stringResource(id = R.string.activity_main_total),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
            )
            Amount(amount = income + bills, Modifier)
        }
    }
}
