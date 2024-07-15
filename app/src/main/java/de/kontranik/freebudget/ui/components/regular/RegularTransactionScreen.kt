package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionsUiState
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Calendar

@Composable
fun RegularTransactionScreen(
    drawerState: DrawerState,
    navigateToEdit: (type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    regularTransactionViewModel: RegularTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val monthState by regularTransactionViewModel.getMonth().observeAsState(
        0
    )
    val uiState = regularTransactionViewModel.regularTRansactionsUiState.observeAsState(
        RegularTransactionsUiState()
    )

    var income = 0.0
    var bills = 0.0

    for (transaction in uiState.value.itemList) {
        if (transaction.isDateInScope(Calendar.getInstance().timeInMillis)) {
            if (transaction.amount > 0) {
                income += transaction.amount
            } else {
                bills += transaction.amount
            }
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = { AppBar(
            title = R.string.regular,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!listState.isScrollInProgress) FabRegularList(onAdd = { type ->
                navigateToEdit(type, null)
            })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            monthState.let {
                MonthSelector(
                    month = it,
                    onPrev = { regularTransactionViewModel.prevMonth() },
                    onNext = { regularTransactionViewModel.nextMonth() },
                    modifier = modifier
                )
            }
            RegularTransactionSummary(
                income = income,
                bills = bills,
                onPrev = { regularTransactionViewModel.prevMonth() },
                onNext = { regularTransactionViewModel.nextMonth() },
                modifier = modifier)
            Box(modifier = Modifier.padding(bottom = paddingSmall))
            RegularTransactionList(
                state = listState,
                transactions = uiState.value.itemList,
                onClick = { _, item ->
                    navigateToEdit(null, item.id)
                },
                )
        }
    }

}