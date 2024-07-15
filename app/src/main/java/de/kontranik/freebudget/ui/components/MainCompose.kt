package de.kontranik.freebudget.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.kontranik.freebudget.ui.navigation.MainNavOption
import de.kontranik.freebudget.ui.navigation.mainGraph
import de.kontranik.freebudget.ui.components.appdrawer.AppDrawerContent
import de.kontranik.freebudget.ui.components.appdrawer.AppDrawerItemInfo
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.R

@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    AppTheme {
        Surface {
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
                                navController.navigate(onUserPickedOption.name) {
                                    popUpTo(NavRoutes.MainRoute.name)
                                }
                            }
                        }
                    }
                }
            ) {
                NavHost(
                    navController,
                    startDestination = NavRoutes.MainRoute.name
                ) {
                    mainGraph(drawerState, navController)
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
            R.string.overview_list,
            R.drawable.ic_assessment_24dp,
            R.string.overview_list
        ),
        AppDrawerItemInfo(
            MainNavOption.AllTransactionsScreen,
            R.string.all_transactions,
            R.drawable.ic_view_list_24dp,
            R.string.all_transactions
        ),
        AppDrawerItemInfo(
            MainNavOption.AllTransactionsSeparated,
            R.string.all_transactions_separated,
            R.drawable.ic_view_list_24dp,
            R.string.all_transactions_separated
        ),
        AppDrawerItemInfo(
            MainNavOption.RegularTransactions,
            R.string.regular,
            R.drawable.ic_view_list_24dp,
            R.string.regular
        ),
        AppDrawerItemInfo(
            MainNavOption.Categories,
            R.string.activity_category,
            R.drawable.ic_view_agenda,
            R.string.activity_category
        ),
        AppDrawerItemInfo(
            MainNavOption.ToolScreen,
            R.string.tools,
            R.drawable.ic_menu_manage,
            R.string.tools
        ),
        AppDrawerItemInfo(
            MainNavOption.SettingsScreen,
            R.string.app_settings,
            R.drawable.ic_settings_24dp,
            R.string.app_settings
        ),
    )
}