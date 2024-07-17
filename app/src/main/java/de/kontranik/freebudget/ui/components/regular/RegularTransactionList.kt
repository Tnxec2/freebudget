package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.model.RegularTransaction

@Composable
fun RegularTransactionList(
    transactions: List<RegularTransaction>,
    onClick: (position: Int, item: RegularTransaction) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize()) {
        itemsIndexed(transactions) { index, transaction ->
            RegularTransactionItem(
                transaction,
                onClick = {onClick(index, transaction)} ,
                modifier)
            if (index < transactions.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
        }
    }
}