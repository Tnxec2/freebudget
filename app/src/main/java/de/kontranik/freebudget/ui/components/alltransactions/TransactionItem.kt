package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.components.shared.Amount
import de.kontranik.freebudget.ui.helpers.DateUtils
import de.kontranik.freebudget.ui.theme.paddingSmall


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onEditPlanned: () -> Unit,
    onDelete: () -> Unit,
    marked: Boolean,
    modifier: Modifier = Modifier
) {

    val dateString =
        if (transaction.date > 0) DateUtils.getDateMedium(transaction.date) else stringResource(R.string.not_setted)
    val haptics = LocalHapticFeedback.current
    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(
        Modifier
            .background(color = if (marked) MaterialTheme.colorScheme.inversePrimary else Color.Transparent)
    ) {
        Column(
            Modifier
                .padding(start = paddingSmall, top = paddingSmall/2, bottom = paddingSmall/2)
                .weight(1f)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        showPopup = true
                    }
                )
        ) {
            Row(
                modifier.fillMaxWidth()
            ) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = modifier
                        .weight(1f)
                )
                Amount(amount = transaction.amountPlanned, modifier)
                Amount(amount = transaction.amountFact, modifier)
            }
            Text(
                text = stringResource(
                    id = R.string.subTitleTransaction,
                    dateString,
                    transaction.category
                ),
                modifier = modifier
                    .fillMaxWidth()
            )

        }
        Column {
            IconButton(onClick = { showPopup = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "open popup menu")
            }
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
private fun TransactionItemPreview() {
    TransactionItem(
        transaction = Transaction(
            null, null, "Test", "Category",
            DateUtils.now(), 10.25, 58.23, DateUtils.now(), 0L, null
        ),
        onClick = { }, onEdit = {}, onEditPlanned = {}, onDelete = {}, marked = false
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun TransactionSItemPreviewMinus() {
    TransactionItem(
        transaction = Transaction(
            null, null, "Test", "Category",
            DateUtils.now(), -58.23, -44.20, DateUtils.now(), 0L, null
        ),
        onClick = { }, onEdit = {}, onEditPlanned = {}, onDelete = {}, marked = false
    )
}