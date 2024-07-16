package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun AllTransactionSeparatedScreen(
    drawerState: DrawerState,
    navigateToEdit: (type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    queryState: State<TransactionQuery>,
    uiState: State<TransactionsUiState>,
    prevMonth: ()-> Unit,
    nextMonth: ()-> Unit,
    planRegular: ()-> Unit,
) {

    val listStateLeft = rememberLazyListState()
    val listStateRight = rememberLazyListState()

    Scaffold(
        topBar = { AppBar(
            title = R.string.all_transactions_separated,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!listStateLeft.isScrollInProgress && !listStateRight.isScrollInProgress)
                FabNormalList(onAdd = { type ->
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
            queryState.value.let {
                MonthSelector(
                    year = it.year,
                    month = it.month,
                    onPrev = { prevMonth() },
                    onNext = { nextMonth() },
                    modifier = modifier
                )
            }
            if (uiState.value.itemList.isEmpty()) {
                Button(
                    onClick = { planRegular() },
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
                stateLeft = listStateLeft,
                stateRight = listStateRight,
            )
        }
    }

}
