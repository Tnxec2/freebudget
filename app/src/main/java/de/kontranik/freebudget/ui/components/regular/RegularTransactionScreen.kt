package de.kontranik.freebudget.ui.components.regular

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.database.viewmodel.RegularTransactionsUiState
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.helpers.DateUtils

@Composable
fun RegularTransactionScreen(
    drawerState: DrawerState,
    navigateToEdit: (month: Int?, type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    monthState: State<Int>,
    uiState: State<RegularTransactionsUiState>,
    prevMonth: () -> Unit,
    nextMonth: () -> Unit,
) {

    var income = 0.0
    var incomeOnlyMonth = 0.0
    var bills = 0.0
    var billsOnlyMonth = 0.0

    for (transaction in uiState.value.itemList) {
        if (transaction.isDateInScope(DateUtils.now())) {
            if (transaction.amount > 0) {
                income += transaction.amount
                if (transaction.month == monthState.value) incomeOnlyMonth += transaction.amount
            } else {
                bills += transaction.amount
                if (transaction.month == monthState.value) billsOnlyMonth += transaction.amount
            }
        }
    }

    OrientationChangesHandler(portraitLayout = {
        RegularTransactionScreenPortrait(
            drawerState = drawerState,
            navigateToEdit = navigateToEdit,
            monthState = monthState,
            uiState = uiState,
            prevMonth = prevMonth,
            nextMonth = nextMonth,
            modifier = modifier,
            income = income,
            bills = bills,
            incomeOnlyMonth = incomeOnlyMonth,
            billsOnlyMonth = billsOnlyMonth
        )
    }, landscapeLayout = {
        RegularTransactionScreenLandscape(
            drawerState = drawerState,
            navigateToEdit = navigateToEdit,
            monthState = monthState,
            uiState = uiState,
            prevMonth = prevMonth,
            nextMonth = nextMonth,
            modifier = modifier,
            income = income,
            bills = bills,
            incomeOnlyMonth = incomeOnlyMonth,
            billsOnlyMonth = billsOnlyMonth
        )
    })

}