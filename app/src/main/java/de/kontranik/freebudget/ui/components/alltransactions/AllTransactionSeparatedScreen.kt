package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.settings.SettingsViewModel
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun AllTransactionSeparatedScreen(
    drawerState: DrawerState,
    navigateToEdit: (type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val queryState by transactionViewModel.query.observeAsState(
        TransactionQuery()
    )

    val uiState = transactionViewModel.transactionsUiState.observeAsState(
        TransactionsUiState()
    )

    var showFab by remember {
        mutableStateOf(true)
    }

    Scaffold(
        topBar = { AppBar(
            title = R.string.all_transactions_separated,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (showFab) FabNormalList(onAdd = { type ->
                navigateToEdit(type, null)
            })
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            queryState.let {
                MonthSelector(
                    year = it.year,
                    month = it.month,
                    onPrev = { transactionViewModel.prevMonth() },
                    onNext = { transactionViewModel.nextMonth() },
                    modifier = modifier
                )
            }
            if (uiState.value.itemList.isEmpty()) {
                Button(
                    onClick = { transactionViewModel.planRegular() },
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {
                    Text(
                        text = stringResource(id = R.string.put_regular).uppercase(),
                        modifier = modifier)
                }
            }
            AllTransactionSeparatedList(
                transactions = uiState.value.itemList,
                onClick = { _, item ->
                    navigateToEdit(null, item.id)
                },
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                showFab = false
                            },
                            onDrag = { pi, offset -> },
                            onDragEnd = { showFab = true },
                            onDragCancel = { showFab = true }
                        )
                    })
        }
    }

}
