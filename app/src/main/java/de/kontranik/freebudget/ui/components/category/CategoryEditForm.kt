package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@Composable
fun CategoryEditForm(
    modifier: Modifier = Modifier,
    viewModel: CategoryItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val categoryDetails = viewModel.transactionItemUiState.categoryDetails

    val openConfirmDeleteDialog = remember { mutableStateOf(false) }

    fun onValueChange(item: CategoryDetails) {
        viewModel.updateUiState(item)
    }

    fun save() {
        viewModel.save(categoryDetails.toCategrory())
        viewModel.updateUiState(CategoryDetails())
    }

    fun delete() {
        viewModel.delete(categoryDetails.toCategrory())
        viewModel.updateUiState(CategoryDetails())
        openConfirmDeleteDialog.value = false
    }

    fun onClear() {
        viewModel.clearItem()
    }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoryDetails.name,
                onValueChange = { onValueChange(categoryDetails.copy(name = it)) },
                label = {
                    Text(
                        stringResource(
                            id = if (categoryDetails.id != null)  R.string.category_name else R.string.category_name_new
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            IconButton(onClick = { onClear() }) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "clear", modifier = Modifier.size(24.dp),)
            }
        }
        Spacer(modifier = Modifier.height(paddingSmall))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            if (categoryDetails.id != null) {
                OutlinedButton(
                    onClick = {
                        openConfirmDeleteDialog.value = true
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                enabled = categoryDetails.name.isNotEmpty(),
                onClick = {
                    save()
                }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }

    }

    when {
        openConfirmDeleteDialog.value -> {
            DeleteCategoryDialog(
                name = categoryDetails.name,
                onDismissRequest = { openConfirmDeleteDialog.value = false },
                onConfirmation = {
                    coroutineScope.launch {
                        delete()
                    }
                }
            )
        }
    }
}


@Composable
fun DeleteCategoryDialog(
    name: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(paddingMedium),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.confirm_category_delete, name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.padding(paddingMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(paddingSmall),
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(paddingSmall),
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}