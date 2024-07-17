package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun TransactionItemPopupMenu(
    transaction: Transaction,
    onEdit: () -> Unit,
    onEditPlanned: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier) {
    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
        ),
        // to dismiss on click outside
        onDismissRequest = { onClose() },
    ) {
        Column(
            modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(paddingSmall)
                        .weight(1f)
                )
                IconButton(onClick = { onClose() }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "close")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onEdit() }) {
                Text(
                    text = stringResource(id = R.string.activity_transaction_edit),
                    modifier = Modifier
                        .padding(paddingSmall)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onEditPlanned() }) {
                Text(
                    text = stringResource(id = R.string.activity_transaction_edit_planned),
                    modifier = Modifier
                        .padding(paddingSmall)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onDelete() }) {
                Text(
                    text = stringResource(id = R.string.activity_transaction_delete),
                    modifier = Modifier
                        .padding(paddingSmall)
                )
            }
        }
    }
}
