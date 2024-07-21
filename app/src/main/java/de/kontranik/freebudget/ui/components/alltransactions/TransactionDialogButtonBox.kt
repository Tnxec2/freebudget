package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingSmall


@Composable
fun TransitionDialogButtonBox(
    canDelete: Boolean, // itemDetails.id != null
    onDelete: ()-> Unit, // itemDetails.id.let {coroutineScope.launch {    viewModel.delete(it); navigateBack() } }
    onSave: () -> Unit, //coroutineScope.launch { save() }
    onSaveAndExit: () -> Unit, // coroutineScope.launch { save(); navigateBack() }
    onClose: () -> Unit, // coroutineScope.launch {navigateBack()}
    onCopy: () -> Unit, // onValueChange(itemDetails.copy(id = null))
    modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingSmall)
        ) {
            OutlinedButton(
                onClick = {
                    onSaveAndExit()
                },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_saveAclose).uppercase())
            }
            OutlinedButton(
                onClick = {
                    onClose()
                },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_close).uppercase())
            }
            if (canDelete) OutlinedButton(
                onClick = {
                    onDelete()
                },
                Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_delete).uppercase())
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    onSave()
                },
                Modifier
                    .weight(1f)
                    .padding(end = paddingSmall)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_save).uppercase())
            }
            OutlinedButton(
                onClick = { onCopy() },
                Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.activity_transaction_copy).uppercase())
            }
        }
    }
}

@Preview
@Composable
private fun TransitionDialogButtonBoxPreview() {
    AppTheme {
        Surface(
            onClick = { },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            TransitionDialogButtonBox(
                canDelete = true,
                onDelete = { },
                onSave = { },
                onSaveAndExit = { },
                onClose = { },
                onCopy = { })
        }
    }
}