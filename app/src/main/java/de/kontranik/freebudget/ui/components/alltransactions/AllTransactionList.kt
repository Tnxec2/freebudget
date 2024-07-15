package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.model.Transaction

@Composable
fun AllTransactionList(
    transactions: List<Transaction>,
    markLastEdited: Boolean,
    onClick: (position: Int, item: Transaction) -> Unit,
    modifier: Modifier = Modifier) {

    var lastEditedId: Long? = null

    if (markLastEdited && transactions.isNotEmpty()) {
        lastEditedId = transactions.first().id
        val lastEditedDate = transactions.first().dateEdit
        transactions.forEach { item -> if (item.dateEdit > lastEditedDate) lastEditedId = item.id}
    }

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(transactions) { index, transaction ->
            TransactionItem(
                transaction,
                onClick = {onClick(index, transaction)} ,
                marked = markLastEdited && transaction.id == lastEditedId,
                modifier)
            if (index < transactions.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
        }
    }
}