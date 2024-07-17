package de.kontranik.freebudget.ui.components.regular

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.navigation.NavigationDestination

object RegularTransactionItemDestination : NavigationDestination {
    override val route = "RegularTransactionDialog"
    override val titleRes = R.string.regular
    const val MONTH_ARG = "month"
    const val ITEM_ID_ARG = "itemId"
    const val ITEM_TYPE_ARG = "type"
    val routeWithArgs = "$route/{$MONTH_ARG}/{$ITEM_TYPE_ARG}/{$ITEM_ID_ARG}"
}

@Composable
fun RegularTransactionDialog(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
) {

    OrientationChangesHandler(portraitLayout = {
        RegularTransactionDialogPortrait(
            drawerState = drawerState,
            navigateBack = { navigateBack() })
    }, landscapeLayout = {
        RegularTransactionDialogLandscape(
            drawerState = drawerState,
            navigateBack = { navigateBack() })
    })
}



