package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.ui.navigation.NavigationDestination
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.DropdownList
import de.kontranik.freebudget.ui.components.shared.DropdownListContent
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch

object RegularTransactionItemDestination : NavigationDestination {
    override val route = "RegularTransactionDialog"
    override val titleRes = R.string.regular
    const val ITEM_ID_ARG = "itemId"
    const val ITEM_TYPE_ARG = "type"
    val routeWithArgs = "$route/{$ITEM_TYPE_ARG}/{$ITEM_ID_ARG}"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegularTransactionDialog(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegularTransactionItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    categoryViewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val itemDetails = viewModel.regularTransactionItemUiState.itemDetails
    val categorys = categoryViewModel.distinctCategoryNames.observeAsState(listOf())


    fun onValueChange(item: RegularTransactionItem) {
        viewModel.updateUiState(item)
    }

    val coroutineScope = rememberCoroutineScope()

    val amountType = listOf(
        stringResource(id = R.string.activity_main_receipts),
        stringResource(id = R.string.activity_main_spending)
    )

    var showCategoryDropdown by rememberSaveable {
        mutableStateOf(false)
    }
    var searchCategory by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val scrollStateCategory = rememberScrollState()

    fun save() {
        viewModel.save(itemDetails.toRegularTransaction())
    }

    val (descFocus, catFocus, dayFocus, amountFocus) = FocusRequester.createRefs()

    LaunchedEffect(itemDetails.id, Unit) {
        if (itemDetails.id == null)
            descFocus.requestFocus()
        else
            amountFocus.requestFocus()
    }

    Scaffold(
        topBar = { AppBar(
            title = if (itemDetails.id != null) R.string.regular else R.string.new_regular_transaction,
            drawerState = drawerState) },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = paddingSmall)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                singleLine = true,
                value = itemDetails.description,
                label = { Text(stringResource(R.string.activity_transaction_description)) },
                onValueChange = { onValueChange(itemDetails.copy(description = it)) },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(descFocus),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions( onNext = {catFocus.requestFocus()})
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall)
            ) {
                OutlinedTextField(
                    singleLine = true,
                    value = itemDetails.category,
                    label = { Text(stringResource(R.string.activity_category)) },
                    onValueChange = {
                        onValueChange(itemDetails.copy(category = it))
                        searchCategory = it
                        showCategoryDropdown = it.isNotEmpty()
                    },
                    modifier = Modifier.weight(1f)
                        .focusRequester(catFocus),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions( onNext = {amountFocus.requestFocus()})
                )
                IconButton(
                    onClick = {
                        if (showCategoryDropdown.not()) searchCategory = null;
                        showCategoryDropdown = showCategoryDropdown.not()
                    }) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "open")
                }
            }
            Box {
                if (showCategoryDropdown) {
                    DropdownListContent(
                        itemList = categorys.value
                            .filter {
                                searchCategory == null || it.lowercase()
                                    .startsWith(searchCategory!!.lowercase())
                            },
                        scrollState = scrollStateCategory,
                        onItemClick = { _, value -> onValueChange(itemDetails.copy(category = value)) },
                        onClose = { showCategoryDropdown = false; }
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall)
            ) {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier.weight(1f).focusRequester(amountFocus),
                    label = { Text(stringResource(id = R.string.activity_transaction_amount)) },
                    value = TextFieldValue(itemDetails.amount, selection = TextRange(itemDetails.amount.length)),
                    onValueChange = {
                        onValueChange(itemDetails.copy(amount = it.text))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions( onNext = {dayFocus.requestFocus()})
                )
                Column {
                    amountType.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = ((text == amountType[0] && itemDetails.isIncome) || (text == amountType[1] && !itemDetails.isIncome)),
                                    onClick = {
                                        onValueChange(itemDetails.copy(isIncome = text == amountType[0]))
                                    }
                                )
                                .padding(start = paddingSmall)

                        ) {
                            RadioButton(
                                selected = ((text == amountType[0] && itemDetails.isIncome) || (text == amountType[1] && !itemDetails.isIncome)),
                                onClick = {
                                    onValueChange(itemDetails.copy(isIncome = text == amountType[0]))
                                },
                                modifier = Modifier
                                    //HERE YOU GO
                                    .height(30.dp)
                                    .padding(horizontal = 0.dp, vertical = 0.dp)
                            )
                            Text(
                                text = text,
                            )
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    singleLine = true,
                    value = TextFieldValue(itemDetails.day, selection = TextRange(itemDetails.day.length)),
                    label = { Text(stringResource(id = R.string.day)) },
                    onValueChange = {
                        onValueChange(itemDetails.copy(day = it.text))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier
                        .width(80.dp)
                        .padding(end = paddingSmall)
                        .focusRequester(dayFocus)
                )

                DropdownList(
                    itemList = stringArrayResource(id = R.array.months).toList(),
                    selectedIndex = itemDetails.month,
                    onItemClick = { pos, _ -> onValueChange(itemDetails.copy(month = pos)) },
                )
            }
            OutlinedTextField(
                value = itemDetails.note,
                label = { Text(stringResource(R.string.activity_transaction_note)) },
                onValueChange = { onValueChange(itemDetails.copy(note = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = paddingSmall),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            save()
                            navigateBack()
                        }
                    },
                    Modifier
                        .weight(1f)
                        .padding(end = paddingSmall)
                ) {
                    Text(text = stringResource(id = R.string.activity_transaction_saveAclose).uppercase())
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            navigateBack()
                        }
                    },
                    Modifier
                        .weight(1f)
                        .padding(end = paddingSmall)
                ) {
                    Text(text = stringResource(id = R.string.activity_transaction_close).uppercase())
                }
                if (itemDetails.id != null) Button(
                    onClick = { itemDetails.id.let {
                        coroutineScope.launch {
                            viewModel.delete(it)
                            navigateBack()
                        }
                    } },
                    Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.activity_transaction_delete).uppercase())
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            save()
                        }
                    },
                    Modifier
                        .weight(1f)
                        .padding(end = paddingSmall)
                ) {
                    Text(text = stringResource(id = R.string.activity_transaction_save).uppercase())
                }
                Button(
                    onClick = { onValueChange(itemDetails.copy(id = null)) },
                    Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.activity_transaction_copy).uppercase())
                }
            }
        }
    }
}

