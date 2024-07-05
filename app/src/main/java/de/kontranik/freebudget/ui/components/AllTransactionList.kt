package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction

@Composable
fun AllTransactionList(
    transactions: State<List<Transaction>>,
    markLastEdited: Boolean,
    lastEditedId: State<Long?>,
    onClick: (position: Int, item: Transaction) -> Unit,
    modifier: Modifier = Modifier) {

    LazyColumn(Modifier.fillMaxSize()) {

        itemsIndexed(transactions.value) { index, transaction ->
            TransactionItem(
                transaction,
                onClick = {onClick(index, transaction)} ,
                marked = markLastEdited && transaction.id == lastEditedId.value,
                modifier)
            if (index < transactions.value.lastIndex)
                HorizontalDivider(color = colorResource(id = R.color.colorBackground), thickness = 0.5.dp)
        }
    }
}