package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun RegularTransactionDialogButtonBox(
    canDelete: Boolean, // itemDetails.id != null
    onDelete: () -> Unit, // itemDetails.id?.let {id -> coroutineScope.launch {    viewModel.delete(id); navigateBack() } }
    onSave: () -> Unit, //coroutineScope.launch { save() }
    onSaveAndExit: () -> Unit, // coroutineScope.launch { save(); navigateBack() }
    onClose: () -> Unit, // coroutineScope.launch {navigateBack()}
    onCopy: () -> Unit, // onValueChange(itemDetails.copy(id = null))
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingSmall)
                .height(intrinsicSize = IntrinsicSize.Max)
        ) {
            Button(
                onClick = {
                    onSaveAndExit()
                },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
                    .fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_saveAclose).uppercase())
            }
            Button(
                onClick = {
                    onClose()
                },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_close).uppercase())
            }
            if (canDelete) Button(
                onClick = { onDelete() },
                Modifier.weight(1f)
                    .fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_delete).uppercase())
            }
        }
        Row(Modifier.fillMaxWidth().height(intrinsicSize = IntrinsicSize.Max)) {
            Button(
                onClick = { onSave() },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
                    .fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_save).uppercase())
            }
            Button(
                onClick = { onCopy() },
                Modifier.weight(1f)
                    .fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_copy).uppercase())
            }
        }
    }
}