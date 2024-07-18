package de.kontranik.freebudget.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.kontranik.freebudget.ui.navigation.MainNavOption
import de.kontranik.freebudget.ui.navigation.mainGraph
import de.kontranik.freebudget.ui.components.appdrawer.AppDrawerContent
import de.kontranik.freebudget.ui.components.appdrawer.AppDrawerItemInfo
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.settings.SettingsViewModel
import de.kontranik.freebudget.ui.components.tools.ToolsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val scope = rememberCoroutineScope()

    val closeDrawer: () -> Unit = {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        else
            scope.launch { navController.popBackStack() }
    }

    val transactionViewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val regularTransactionViewModel: RegularTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val categoryViewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val toolsViewModel: ToolsViewModel = viewModel(factory = AppViewModelProvider.Factory)

    AppTheme {
        Surface {
            BackHandler(enabled = drawerState.isOpen, onBack = closeDrawer)
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawerContent(
                        drawerState = drawerState,
                        menuItems = DrawerParams.drawerButtons,
                        defaultPick = MainNavOption.Overview
                    ) { onUserPickedOption ->
                        when (onUserPickedOption) {
                            MainNavOption.Overview,
                            MainNavOption.AllTransactionsScreen,
                            MainNavOption.AllTransactionsSeparated,
                            MainNavOption.RegularTransactions,
                            MainNavOption.Categories,
                            MainNavOption.ToolScreen,
                            MainNavOption.SettingsScreen, -> {
                                navController.navigate(onUserPickedOption.name)
                            }
                        }
                    }
                }
            ) {
                NavHost(
                    navController,
                    startDestination = NavRoutes.MainRoute.name
                ) {
                    mainGraph(drawerState, navController, transactionViewModel, regularTransactionViewModel, categoryViewModel, settingsViewModel, toolsViewModel)
                }
            }
        }
    }
}

enum class NavRoutes {
    MainRoute,
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            MainNavOption.Overview,
            R.string.title_overview,
            R.drawable.ic_assessment_24dp,
            R.string.title_overview
        ),
        AppDrawerItemInfo(
            MainNavOption.AllTransactionsScreen,
            R.string.title_all_transactions,
            R.drawable.ic_view_list_24dp,
            R.string.title_all_transactions
        ),
        AppDrawerItemInfo(
            MainNavOption.AllTransactionsSeparated,
            R.string.title_all_transactions_separated,
            R.drawable.ic_view_list_24dp,
            R.string.title_all_transactions_separated
        ),
        AppDrawerItemInfo(
            MainNavOption.RegularTransactions,
            R.string.title_regular_transactions,
            R.drawable.ic_view_list_24dp,
            R.string.title_regular_transactions
        ),
        AppDrawerItemInfo(
            MainNavOption.Categories,
            R.string.title_category,
            R.drawable.ic_view_agenda,
            R.string.title_category
        ),
        AppDrawerItemInfo(
            MainNavOption.ToolScreen,
            R.string.title_tools,
            R.drawable.ic_menu_manage,
            R.string.title_tools
        ),
        AppDrawerItemInfo(
            MainNavOption.SettingsScreen,
            R.string.title_settings,
            R.drawable.ic_settings_24dp,
            R.string.title_settings
        ),
    )
}
