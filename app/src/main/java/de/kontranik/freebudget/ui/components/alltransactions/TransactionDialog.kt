package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.navigation.NavigationDestination


object TransactionItemDestination : NavigationDestination {
    override val route = "TransactionDialog"
    override val titleRes = R.string.title_activity_transaction
    const val ITEM_ID_ARG = "itemId"
    const val ITEM_TYPE_ARG = "type"
    val routeWithArgs = "$route/{$ITEM_TYPE_ARG}/{$ITEM_ID_ARG}"
}

@Composable
fun TransactionDialog(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
) {
    
    OrientationChangesHandler(portraitLayout = {
        TransactionDialogPortrait(
            drawerState = drawerState,
            navigateBack = { navigateBack() })
    }, landscapeLayout = {
        TransactionDialogLandscape(
            drawerState = drawerState,
            navigateBack = { navigateBack() })
    })

}

