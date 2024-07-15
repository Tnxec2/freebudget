package de.kontranik.freebudget.ui.components.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.components.shared.swipableModifier
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlin.math.abs

const val column1Weight = .5f //
const val column2Weight = .25f //
const val column3Weight = .25f //

@Composable
fun OverviewSummary(
    transactions: List<Transaction>,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    ) {

    var incomePlanned = 0.0
    var incomePlannedFact = 0.0
    var incomeUnplannedFact = 0.0
    var billsPlanned = 0.0
    var billsPlannedFact = 0.0
    var billsUnplannedFact = 0.0
    var incomePlannedRest = 0.0
    var billsPlannedRest = 0.0

    transactions.forEach { transaction ->
        if (transaction.amountPlanned > 0)
            incomePlanned += transaction.amountPlanned
        else
            billsPlanned += abs(transaction.amountPlanned)

        if (transaction.amountFact > 0)
            if (transaction.amountPlanned > 0)
                incomePlannedFact += transaction.amountFact
            else
                incomeUnplannedFact += transaction.amountFact
        else if (transaction.amountFact < 0)
            if (transaction.amountPlanned < 0)
                billsPlannedFact += abs(transaction.amountFact)
            else
                billsUnplannedFact += abs(transaction.amountFact)
        else { // transaction.amountFact == 0
            if (transaction.amountPlanned > 0)
                incomePlannedRest += transaction.amountPlanned
            if (transaction.amountPlanned < 0)
                billsPlannedRest += abs(transaction.amountPlanned)
        }
    }

    Column(
        swipableModifier(modifier = modifier, onPrev, onNext)
            .fillMaxWidth()
            .padding(paddingSmall)
    ) {

        Row(Modifier.fillMaxWidth()) {
            Text(text = "", Modifier.weight(column1Weight))
            Text(
                text = stringResource(id = R.string.activity_main_planned),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(column2Weight)
            )
            Text(text = stringResource(id = R.string.activity_main_fact),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.activity_main_receipts_planned), Modifier.weight(
                column1Weight
            ))
            Amount(amount = incomePlanned, modifier = Modifier.weight(column2Weight))
            Amount(amount = incomePlannedFact, modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.activity_main_receipts_unplanned), Modifier.weight(
                column1Weight
            ))
            Text(text = "", Modifier.weight(column2Weight))
            Amount(amount = incomeUnplannedFact, modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.activity_main_spending_planned), Modifier.weight(
                column1Weight
            ))
            Amount(amount = -billsPlanned, modifier = Modifier.weight(column2Weight))
            Amount(amount = -billsPlannedFact, modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.activity_main_spending_unplanned), Modifier.weight(
                column1Weight
            ))
            Text(text = "", Modifier.weight(column2Weight))
            Amount(amount = -billsUnplannedFact, modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.activity_main_total),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(column1Weight))
            Amount(
                amount = incomePlanned - billsPlanned,
                modifier = Modifier.weight(column2Weight))
            Amount(
                amount = incomePlannedFact + incomeUnplannedFact - billsPlannedFact - billsUnplannedFact,
                modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.receipts_planned_rest), Modifier.weight(
                column1Weight
            ))
            Amount(amount = incomePlannedRest, modifier = Modifier.weight(column2Weight))
            Text(text = "", modifier = Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.spending_planned_rest), Modifier.weight(
                column1Weight
            ))
            Amount(amount = -billsPlannedRest, modifier = Modifier.weight(column2Weight))
            Text(text = "", Modifier.weight(column3Weight))
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.total_diff),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(column1Weight))
            Text(text = "", Modifier.weight(column2Weight))
            Amount(amount = incomePlannedRest - billsPlannedRest + (incomePlannedFact + incomeUnplannedFact - billsPlannedFact - billsUnplannedFact), modifier = Modifier.weight(
                column3Weight
            ))
        }
    }
}