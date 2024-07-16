package de.kontranik.freebudget.ui.components.overview

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.components.shared.TransactionType

@Composable
fun OverviewScreen(
    drawerState: DrawerState,
    navigateToNewTransaction: (type: TransactionType?) -> Unit,
    navToAllTransactions: (categoryName: String?) -> Unit,
    navToAllTransactionsSeparated: () -> Unit,
    navToRegularTransactions: () -> Unit,
    modifier: Modifier = Modifier,
    queryState: State<TransactionQuery>,
    uiState: State<TransactionsUiState>,
    prevMonth: () -> Unit,
    nextMonth: () -> Unit,
) {

    OrientationChangesHandler(portraitLayout = {
        OverviewScreenPortrait(
            drawerState = drawerState,
            navigateToNewTransaction = navigateToNewTransaction,
            navToAllTransactions = navToAllTransactions,
            navToAllTransactionsSeparated = navToAllTransactionsSeparated,
            navToRegularTransactions = navToRegularTransactions,
            queryState = queryState,
            uiState = uiState,
            prevMonth = prevMonth,
            nextMonth = nextMonth,
            modifier = modifier
        )
    }, landscapeLayout = {
        OverviewScreenLandscape(
            drawerState = drawerState,
            navigateToNewTransaction = navigateToNewTransaction,
            navToAllTransactions = navToAllTransactions,
            navToAllTransactionsSeparated = navToAllTransactionsSeparated,
            navToRegularTransactions = navToRegularTransactions,
            queryState = queryState,
            uiState = uiState,
            prevMonth = prevMonth,
            nextMonth = nextMonth,
            modifier = modifier
        )
    }
    )
}