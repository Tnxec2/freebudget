package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun RegularTransactionList(
    transactions: State<List<RegularTransaction>>,
    onClick: (position: Int, item: RegularTransaction) -> Unit,
    modifier: Modifier = Modifier) {

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(transactions.value) { index, transaction ->
            RegularTransactionItem(
                transaction,
                onClick = {onClick(index, transaction)} ,
                modifier)
            if (index < transactions.value.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
        }
    }
}