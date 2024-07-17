package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun RegularTransactionDialogPortrait(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegularTransactionItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val itemDetails = viewModel.regularTransactionItemUiState.itemDetails

    fun onValueChange(item: RegularTransactionItem) {
        viewModel.updateUiState(item)
    }

    val coroutineScope = rememberCoroutineScope()

    fun save() {
        viewModel.save(itemDetails.toRegularTransaction())
    }

    Scaffold(
        topBar = { AppBar(
            title = if (itemDetails.id != null) R.string.regular else R.string.new_regular_transaction,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(modifier = Modifier.padding(padding).padding(paddingSmall)) {
            RegularTransactionDialogForm(itemDetails = itemDetails, onValueChange = { onValueChange(it)})
            RegularTransactionDialogButtonBox(
                canDelete = itemDetails.id != null,
                onDelete = { itemDetails.id?.let { id -> coroutineScope.launch { viewModel.delete(id); navigateBack() } } },
                onSave = { coroutineScope.launch { save() } },
                onSaveAndExit = { coroutineScope.launch { save(); navigateBack() } },
                onClose = { coroutineScope.launch {navigateBack()} },
                onCopy = { onValueChange(itemDetails.copy(id = null)) })
        }
    }
}

