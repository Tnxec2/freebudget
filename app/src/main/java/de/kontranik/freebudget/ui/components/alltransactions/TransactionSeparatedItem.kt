package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.helpers.DateUtils
import de.kontranik.freebudget.ui.theme.paddingSmall


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionSeparatedItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onEditPlanned: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    val dateString =
        if (transaction.date > 0) DateUtils.getDateMedium(transaction.date) else stringResource(R.string.not_setted)
    val haptics = LocalHapticFeedback.current
    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier
                .weight(1f)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showPopup = true
                    }
                )
                .padding(horizontal = paddingSmall, vertical = paddingSmall/2)
        ) {
            Row(
                modifier.fillMaxWidth()
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier
                        .weight(1f)
                )
                Amount(
                    amount = if (transaction.amountFact != 0.0) transaction.amountFact else transaction.amountPlanned,
                    modifier
                )
            }
            Text(
                text = stringResource(
                    id = R.string.subTitleTransaction,
                    dateString,
                    transaction.category
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = modifier
                    .fillMaxWidth()
            )
        }
        if (showPopup) {
            TransactionItemPopupMenu(
                transaction = transaction,
                onEdit = { showPopup = false; onEdit() },
                onEditPlanned = { showPopup = false; onEditPlanned() },
                onDelete = { showPopup = false; onDelete() },
                onClose = { showPopup = false })
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSepartedItemPreview() {
    TransactionSeparatedItem(
        transaction = Transaction(
            null, null, "Test", "Category",
            DateUtils.now(), 0.0, 58.23, DateUtils.now(), 0L, null
        ),
        onClick = { }, onEdit = {}, onEditPlanned = {}, onDelete = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSepartedItemPreviewMinus() {
    TransactionSeparatedItem(
        transaction = Transaction(
            null, null, "Test", "Category",
            DateUtils.now(), -58.23, 0.0, DateUtils.now(), 0L, null
        ),
        onClick = { }, onEdit = {}, onEditPlanned = {}, onDelete = {},
    )
}