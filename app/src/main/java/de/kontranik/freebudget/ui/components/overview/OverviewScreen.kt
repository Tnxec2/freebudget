package de.kontranik.freebudget.ui.components.overview

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.components.shared.PreviewPortraitLandscapeLightDark
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.AppTheme

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

@PreviewPortraitLandscapeLightDark
@Composable
fun OverviewScreenPreview() {
    val queryState = remember {
        mutableStateOf(TransactionQuery())
    }
    val uiState = remember {
        mutableStateOf(TransactionsUiState(
            itemList = listOf(),
        ))
    }
    AppTheme {
        OverviewScreen(
            drawerState = DrawerState(DrawerValue.Closed),
            navigateToNewTransaction = {},
            navToAllTransactions = {},
            navToAllTransactionsSeparated = { },
            navToRegularTransactions = { },
            queryState = queryState,
            uiState = uiState,
            prevMonth = {},
            nextMonth = {})
    }
}
