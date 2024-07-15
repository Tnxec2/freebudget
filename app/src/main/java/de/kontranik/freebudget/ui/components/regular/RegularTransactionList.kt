package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.database.viewmodel.RegularTransactionsUiState
import de.kontranik.freebudget.model.RegularTransaction

@Composable
fun RegularTransactionList(
    transactions: List<RegularTransaction>,
    onClick: (position: Int, item: RegularTransaction) -> Unit,
    modifier: Modifier = Modifier) {

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(transactions) { index, transaction ->
            RegularTransactionItem(
                transaction,
                onClick = {onClick(index, transaction)} ,
                modifier)
            if (index < transactions.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
        }
    }
}