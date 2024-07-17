package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.RegularTransactionsUiState
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType


@Composable
fun RegularTransactionScreenLandscape(
    drawerState: DrawerState,
    navigateToEdit: (month: Int?, type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    monthState: State<Int>,
    uiState: State<RegularTransactionsUiState>,
    prevMonth: ()-> Unit,
    nextMonth: ()-> Unit,
    income: Double,
    bills: Double,
) {

    val listState = rememberLazyListState()

    Scaffold(
        topBar = { AppBar(
            title = R.string.title_regular_transactions,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!listState.isScrollInProgress) FabRegularList(onAdd = { type ->
                navigateToEdit(monthState.value, type, null)
            })
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { padding ->
        Row(modifier = modifier
            .padding(padding)) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(state = rememberScrollState()),
            ) {
                MonthSelector(
                    month = monthState.value,
                    onPrev = { prevMonth() },
                    onNext = { nextMonth() },
                    modifier = modifier
                )
                RegularTransactionSummary(
                    income = income,
                    bills = bills,
                    onPrev = { prevMonth() },
                    onNext = { nextMonth() },
                    modifier = modifier
                )
            }
            Column(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                RegularTransactionList(
                    state = listState,
                    transactions = uiState.value.itemList,
                    onClick = { _, item ->
                        navigateToEdit(null, null, item.id)
                    },
                )
            }
        }
    }

}