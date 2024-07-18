package de.kontranik.freebudget.ui.components.overview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType
import kotlinx.coroutines.launch

@Composable
fun OverviewScreenPortrait(
    drawerState: DrawerState,
    navigateToNewTransaction: (type: TransactionType?) -> Unit,
    navToAllTransactions: (categoryName: String?) -> Unit,
    navToAllTransactionsSeparated: () -> Unit,
    navToRegularTransactions: () -> Unit,
    modifier: Modifier = Modifier,
    queryState: State<TransactionQuery>,
    uiState: State<TransactionsUiState>,
    prevMonth: ()-> Unit,
    nextMonth: ()-> Unit,
    ) {

    val coroutineScope = rememberCoroutineScope()

    var showFab by remember {
        mutableStateOf(true)
    }

    val categoryListState = rememberLazyListState()

    Scaffold(
        topBar = { AppBar(
            title = R.string.title_overview,
            drawerState = drawerState)
        },
        floatingActionButton = {
            if (showFab && !categoryListState.isScrollInProgress) FabOverview(onAdd = { type ->
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
            queryState.value.let {
                MonthSelector(
                    year = it.year,
                    month = it.month,
                    onPrev = { prevMonth() },
                    onNext = { nextMonth() },
                    modifier = modifier
                )
            }
            OverviewSummary(
                transactions = uiState.value.itemList,
                onPrev = { prevMonth() },
                onNext = { nextMonth() }
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
                state = categoryListState,
                categoryList = uiState.value.categorySummary,
                onSelect = { name ->
                    coroutineScope.launch {
                        navToAllTransactions(name)
                    }
                },
                modifier = Modifier.weight(1f),
            )
            OverviewButtonBox(
                onClickAllTransactionsSeparated = navToAllTransactionsSeparated,
                onClickAllTransactions = { navToAllTransactions(null) },
                onClickRegularTransactions = navToRegularTransactions)
        }
    }
}