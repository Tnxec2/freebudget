package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.TransactionQuery
import de.kontranik.freebudget.database.viewmodel.TransactionViewModel
import de.kontranik.freebudget.database.viewmodel.TransactionsUiState
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.settings.SettingsViewModel
import de.kontranik.freebudget.ui.components.shared.MonthSelector
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.navigation.NavigationDestination
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch


object AllTransactionsScreenDestination : NavigationDestination {
    override val route = "AllTransactionsScreen"
    override val titleRes = R.string.all_transactions
    const val CATEGORY_NAME_ARG = "categoryName"
    val routeWithArgs = "$route/{$CATEGORY_NAME_ARG}"
}

@Composable
fun AllTransactionScreen(
    drawerState: DrawerState,
    navigateToEdit: (type: TransactionType?, id: Long?) -> Unit,
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    allTransactionsScreenViewModel: AllTransactionsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val queryState by transactionViewModel.query.observeAsState(
        TransactionQuery()
    )

    val uiState = transactionViewModel.transactionsUiState.observeAsState(
        TransactionsUiState()
    )

    var showOnlyPlanned by rememberSaveable {
        mutableStateOf(false)
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            AppBar(
                titleString = allTransactionsScreenViewModel.getTitle(showOnlyPlanned, context),
                drawerState = drawerState,
                customActions = listOf {
                    Switch(
                        checked = showOnlyPlanned,
                        onCheckedChange = { showOnlyPlanned = it })
                },
                appBarActions = listOf {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.put_regular))
                            },
                            onClick = { coroutineScope.launch {
                                transactionViewModel.planRegular()
                                menuExpanded = false
                            } },
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!listState.isScrollInProgress) FabNormalList(onAdd = { type ->
                navigateToEdit(type, null)
            })
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            queryState.let {
                MonthSelector(
                    year = it.year,
                    month = it.month,
                    onPrev = { transactionViewModel.prevMonth() },
                    onNext = { transactionViewModel.nextMonth() },
                    modifier = modifier
                )
            }
            if (uiState.value.itemList.isEmpty()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            transactionViewModel.planRegular()
                        } },
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {
                    Text(
                        text = stringResource(id = R.string.put_regular).uppercase(),
                        modifier = modifier
                    )
                }
            }
            AllTransactionList(
                transactions = uiState.value.itemList.filter {
                    allTransactionsScreenViewModel.isValid(it, showOnlyPlanned)
                },
                markLastEdited = settingsViewModel.markLastEditedState.value,
                onClick = { _, item ->
                    navigateToEdit(null, item.id)
                },
                state = listState,
                modifier = Modifier
            )
        }
    }
}
