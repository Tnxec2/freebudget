package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun TransactionDialogLandscape(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val itemDetails = viewModel.transactionItemUiState.itemDetails

    fun onValueChange(item: TransactionItemDetails) {
        viewModel.updateUiState(item)
    }

    val coroutineScope = rememberCoroutineScope()

    fun save() {
        viewModel.save(itemDetails.toTransaction())
    }

    Scaffold(
        topBar = {
            AppBar(
                title = if (itemDetails.id != null) R.string.title_activity_transaction else R.string.title_activity_new_transaction,
                drawerState = drawerState
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Row(
            Modifier
                .padding(padding)
                .padding(horizontal = paddingSmall)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TransactionDialogForm(
                    itemDetails = itemDetails,
                    onValueChange = { onValueChange(it) },
                )
            }
            Spacer(modifier = Modifier.width(paddingSmall))
            Column(modifier = Modifier.weight(1f)) {
                TransitionDialogButtonBox(
                    canDelete = itemDetails.id != null,
                    onDelete = {
                        itemDetails.id?.let { id ->
                            coroutineScope.launch {
                                viewModel.delete(
                                    id
                                ); navigateBack()
                            }
                        }
                    },
                    onSave = { coroutineScope.launch { save() } },
                    onSaveAndExit = { coroutineScope.launch { save(); navigateBack() } },
                    onClose = { coroutineScope.launch { navigateBack() } },
                    onCopy = { onValueChange(itemDetails.copy(id = null)) }
                )

            }
        }
    }
}

