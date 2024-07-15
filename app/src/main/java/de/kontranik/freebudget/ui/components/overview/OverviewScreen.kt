package de.kontranik.freebudget.ui.components.overview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.alltransactions.FabNormalList
import de.kontranik.freebudget.ui.components.shared.TransactionType
import kotlinx.coroutines.launch

@Composable
fun OverviewScreen(
    drawerState: DrawerState,
    navigateToNewTransaction: (type: TransactionType?) -> Unit,
    navToAllTransactions: (categoryName: String?) -> Unit,
    navToAllTransactionsSeparated: () -> Unit,
    navToRegularTransactions: () -> Unit,
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ) {

    val coroutineScope = rememberCoroutineScope()

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
            title = R.string.overview_list,
            drawerState = drawerState)
        },
        floatingActionButton = {
            if (showFab) FabOverview(onAdd = { type ->
                navigateToNewTransaction(type)
            })
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            MonthSelector(
                year = queryState.year,
                month = queryState.month,
                onPrev = { transactionViewModel.prevMonth() },
                onNext = { transactionViewModel.nextMonth() },
                modifier = modifier
            )
            OverviewSummary(
                transactions = uiState.value.itemList,
                onPrev = { transactionViewModel.prevMonth() },
                onNext = { transactionViewModel.nextMonth() }
                , modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            showFab = false
                        },
                        onDrag = { pi, offset -> },
                        onDragEnd = { showFab = true },
                        onDragCancel = { showFab = true }
                    )
                }
            )
            OverviewCategorySummary(
                transactions = uiState.value.itemList,
                onSelect = { name ->
                    coroutineScope.launch {
                        navToAllTransactions(name)
                    }
                },
                Modifier.weight(1f)
                    .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            showFab = false
                        },
                        onDrag = { pi, offset -> },
                        onDragEnd = { showFab = true },
                        onDragCancel = { showFab = true }
                    )
                }
            )
            OverviewButtonBox(
                onClickAllTransactionsSeparated = navToAllTransactionsSeparated,
                onClickAllTransactions = { navToAllTransactions(null) },
                onClickRegularTransactions = navToRegularTransactions)
        }
    }
}