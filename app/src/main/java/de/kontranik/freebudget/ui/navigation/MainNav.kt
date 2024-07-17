package de.kontranik.freebudget.ui.navigation


import androidx.compose.material3.DrawerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionViewModel
import de.kontranik.freebudget.database.viewmodel.RegularTransactionsUiState
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
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
import de.kontranik.freebudget.ui.components.settings.SettingsViewModel
import de.kontranik.freebudget.ui.components.tools.ToolsScreen
import de.kontranik.freebudget.ui.components.tools.ToolsViewModel

fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    regularTransactionViewModel: RegularTransactionViewModel,
    categoryViewModel: CategoryViewModel,
    settingsViewModel: SettingsViewModel,
    toolsViewModel: ToolsViewModel,
) {

    navigation(
        startDestination = MainNavOption.Overview.name,
        route = NavRoutes.MainRoute.name) {

        composable(MainNavOption.Overview.name){
            OverviewScreen(
                drawerState = drawerState,
                navigateToNewTransaction = { type ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${null}/${false}")
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
                queryState = transactionViewModel.query.observeAsState(initial = TransactionQuery()),
                uiState = transactionViewModel.transactionsUiState.observeAsState(initial = TransactionsUiState()),
                prevMonth = { transactionViewModel.prevMonth() },
                nextMonth = { transactionViewModel.nextMonth() },
            )
        }

        composable(
            route = AllTransactionsScreenDestination.route,
        ){
            AllTransactionScreen(
                drawerState = drawerState,
                navigateToEdit = { type, id, planned ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}/${planned}")
                },
                queryState = transactionViewModel.query.observeAsState(initial = TransactionQuery()),
                uiState = transactionViewModel.transactionsUiState.observeAsState(initial = TransactionsUiState()),
                prevMonth = { transactionViewModel.prevMonth() },
                nextMonth = { transactionViewModel.nextMonth() },
                planRegular = { transactionViewModel.planRegular() },
                onDelete = { it.id?.let { id -> transactionViewModel.delete(id) } },
                markLastEditedState = settingsViewModel.markLastEditedState
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
                drawerState = drawerState,
                navigateToEdit = { type, id, planned ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}/${planned}")
                },
                queryState = transactionViewModel.query.observeAsState(initial = TransactionQuery()),
                uiState = transactionViewModel.transactionsUiState.observeAsState(initial = TransactionsUiState()),
                prevMonth = { transactionViewModel.prevMonth() },
                nextMonth = { transactionViewModel.nextMonth() },
                planRegular = { transactionViewModel.planRegular() },
                onDelete = { it.id?.let { id -> transactionViewModel.delete(id) } },
                markLastEditedState = settingsViewModel.markLastEditedState,
            )
        }

        composable(MainNavOption.AllTransactionsSeparated.name){
            AllTransactionSeparatedScreen(
                drawerState = drawerState,
                navigateToEdit = { type, id, planned ->
                    navController.navigate("${TransactionItemDestination.route}/${type}/${id}/${planned}")
                },
                queryState = transactionViewModel.query.observeAsState(initial = TransactionQuery()),
                uiState = transactionViewModel.transactionsUiState.observeAsState(initial = TransactionsUiState()),
                prevMonth = { transactionViewModel.prevMonth() },
                nextMonth = { transactionViewModel.nextMonth() },
                planRegular = { transactionViewModel.planRegular() },
                onDelete = { it.id?.let { id -> transactionViewModel.delete(id) } },
            )
        }

        composable(MainNavOption.RegularTransactions.name){
            RegularTransactionScreen(
                drawerState = drawerState,
                navigateToEdit = { month, type, id ->
                    navController.navigate("${RegularTransactionItemDestination.route}/${month}/${type}/${id}")
                },
                monthState = regularTransactionViewModel.getMonth().observeAsState(
                    0
                ),
                uiState = regularTransactionViewModel.regularTRansactionsUiState.observeAsState(
                    RegularTransactionsUiState()
                ),
                prevMonth = { regularTransactionViewModel.prevMonth() },
                nextMonth = { regularTransactionViewModel.nextMonth() },
            )
        }

        composable(MainNavOption.Categories.name){
            CategoryListScreen(
                categoryListState = categoryViewModel.mAllCategorys.observeAsState(initial = listOf()),
                drawerState = drawerState,
            )
        }

        composable(MainNavOption.ToolScreen.name){
            ToolsScreen(
                drawerState = drawerState,
                navigateBack = { navController.navigateUp() },
                toolsViewModel = toolsViewModel,
                )
        }

        composable(MainNavOption.SettingsScreen.name){
            SettingsScreen(
                drawerState = drawerState,
                settingsViewModel = settingsViewModel,
            )
        }

        composable(
            route = RegularTransactionItemDestination.routeWithArgs,
            arguments = listOf(
                navArgument(RegularTransactionItemDestination.MONTH_ARG) { type = NavType.StringType; nullable = true},
                navArgument(RegularTransactionItemDestination.ITEM_ID_ARG) { type = NavType.StringType; nullable = true},
                navArgument(RegularTransactionItemDestination.ITEM_TYPE_ARG) { type = NavType.StringType; nullable = true}
            )
        ){
            RegularTransactionDialog(
                drawerState = drawerState,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = TransactionItemDestination.routeWithArgs,
            arguments = listOf(
                navArgument(TransactionItemDestination.ITEM_ID_ARG) { type = NavType.StringType; nullable = true},
                navArgument(TransactionItemDestination.ITEM_TYPE_ARG) { type = NavType.StringType; nullable = true},
                navArgument(TransactionItemDestination.EDIT_PLANNED_ARG) { type = NavType.StringType},
            )
        ){
            TransactionDialog(
                drawerState = drawerState,
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