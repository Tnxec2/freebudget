package de.kontranik.freebudget.ui.navigation


import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import de.kontranik.freebudget.ui.components.NavRoutes
import de.kontranik.freebudget.ui.components.alltransactions.AllTransactionScreen
import de.kontranik.freebudget.ui.components.alltransactions.AllTransactionSeparatedScreen
import de.kontranik.freebudget.ui.components.alltransactions.AllTransactionsScreenDestination
import de.kontranik.freebudget.ui.components.alltransactions.TransactionDialog
import de.kontranik.freebudget.ui.components.alltransactions.TransactionItemDestination
import de.kontranik.freebudget.ui.components.category.CategoryListScreen
import de.kontranik.freebudget.ui.components.overview.OverviewScreen
import de.kontranik.freebudget.ui.components.regular.RegularTransactionDialog
import de.kontranik.freebudget.ui.components.regular.RegularTransactionItemDestination
import de.kontranik.freebudget.ui.components.regular.RegularTransactionScreen
import de.kontranik.freebudget.ui.components.settings.SettingsScreen
import de.kontranik.freebudget.ui.components.tools.ToolsScreen

fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController
) {
    navigation(
        startDestination = MainNavOption.Overview.name,
        route = NavRoutes.MainRoute.name) {

        composable(MainNavOption.Overview.name){
            OverviewScreen(
                drawerState,
                navigateToNewTransaction = { type ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${null}")
                },
                navToAllTransactionsSeparated = {
                    navController.navigate(MainNavOption.AllTransactionsSeparated.name)
                },
                navToAllTransactions = { categoryName ->
                    navController.navigate(
                        if (categoryName != null)
                            "${AllTransactionsScreenDestination.route}/$categoryName"
                        else
                            AllTransactionsScreenDestination.route
                    )},
                navToRegularTransactions = {navController.navigate(MainNavOption.RegularTransactions.name)},
            )
        }

        composable(
            route = AllTransactionsScreenDestination.route,
        ){
            AllTransactionScreen(
                drawerState,
                navigateToEdit = { type, id ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}")
                }
            )
        }

        composable(
            route = AllTransactionsScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AllTransactionsScreenDestination.CATEGORY_NAME_ARG) {
                    type = NavType.StringType; nullable = true },
            )
        ){
            AllTransactionScreen(
                drawerState,
                navigateToEdit = { type, id ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}")
                }
            )
        }

        composable(MainNavOption.AllTransactionsSeparated.name){
            AllTransactionSeparatedScreen(
                drawerState,
                navigateToEdit = { type, id ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}")
                }
            )
        }

        composable(MainNavOption.RegularTransactions.name){
            RegularTransactionScreen(
                drawerState,
                navigateToEdit = { type, id ->
                    navController.navigate("${RegularTransactionItemDestination.route}/${type}/${id}")
                }
            )
        }

        composable(MainNavOption.Categories.name){
            CategoryListScreen(
                drawerState,
                navigateUp = { navController.navigate(MainNavOption.Overview.name) })
        }

        composable(MainNavOption.ToolScreen.name){
            ToolsScreen(
                drawerState,
                navigateBack = { navController.navigateUp() })
        }

        composable(MainNavOption.SettingsScreen.name){
            SettingsScreen(
                drawerState,
                navigateUp = { navController.navigate(MainNavOption.Overview.name) })
        }

        composable(
            route = RegularTransactionItemDestination.routeWithArgs,
            arguments = listOf(
                navArgument(RegularTransactionItemDestination.ITEM_ID_ARG) { type = NavType.StringType; nullable = true},
                navArgument(RegularTransactionItemDestination.ITEM_TYPE_ARG) { type = NavType.StringType; nullable = true}
            )
        ){
            RegularTransactionDialog(
                drawerState,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = TransactionItemDestination.routeWithArgs,
            arguments = listOf(
                navArgument(TransactionItemDestination.ITEM_ID_ARG) { type = NavType.StringType; nullable = true},
                navArgument(TransactionItemDestination.ITEM_TYPE_ARG) { type = NavType.StringType; nullable = true}
            )
        ){
            TransactionDialog(
                drawerState,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

enum class MainNavOption {
    Overview,
    AllTransactionsScreen,
    AllTransactionsSeparated,
    RegularTransactions,
    Categories,
    ToolScreen,
    SettingsScreen,
}