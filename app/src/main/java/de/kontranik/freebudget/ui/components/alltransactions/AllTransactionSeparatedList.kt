package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount

@Composable
fun AllTransactionSeparatedList(
    transactions: List<Transaction>,
    onClick: (position: Int, item: Transaction) -> Unit,
    modifier: Modifier = Modifier,
    stateLeft: LazyListState = rememberLazyListState(),
    stateRight: LazyListState = rememberLazyListState(),
    ) {

    Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            LazyColumn(
                state = stateLeft,
                modifier = Modifier.weight(1f)) {
                itemsIndexed(transactions.filter {
                    (if (it.amountFact != 0.0) it.amountFact else it.amountPlanned) > 0
                }) { index, transaction ->
                    TransactionSeparatedItem(
                        transaction,
                        onClick = {onClick(index, transaction)} ,
                        modifier)
                    if (index < transactions.lastIndex)
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
            Amount(
                amount = transactions.map{ if (it.amountFact != 0.0) it.amountFact else it.amountPlanned }.filter { it > 0}.sumOf { it },
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(Modifier.weight(1f)) {
            LazyColumn(
                state = stateRight,
                modifier = Modifier.weight(1f)) {
                itemsIndexed(transactions.filter {
                    (if (it.amountFact != 0.0) it.amountFact else it.amountPlanned) < 0
                }) { index, transaction ->
                    TransactionSeparatedItem(
                        transaction,
                        onClick = {onClick(index, transaction)} ,
                        modifier)
                    if (index < transactions.lastIndex)
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
            Amount(
                amount = transactions.map{ if (it.amountFact != 0.0) it.amountFact else it.amountPlanned }.filter { it < 0}.sumOf { it },
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}