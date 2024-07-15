package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.DatePickerButton
import de.kontranik.freebudget.ui.components.shared.DropdownListContent
import de.kontranik.freebudget.ui.navigation.NavigationDestination
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch


object TransactionItemDestination : NavigationDestination {
    override val route = "TransactionDialog"
    override val titleRes = R.string.title_activity_transaction
    const val ITEM_ID_ARG = "itemId"
    const val ITEM_TYPE_ARG = "type"
    val routeWithArgs = "$route/{$ITEM_TYPE_ARG}/{$ITEM_ID_ARG}"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransactionDialog(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    categoryViewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val itemDetails = viewModel.transactionItemUiState.itemDetails

    val categorys = categoryViewModel.mAllCategorys.observeAsState(listOf())

    fun onValueChange(item: TransactionItemDetails) {
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
        viewModel.save(itemDetails.toTransaction())
    }

    val (descField, catFocus, factFocus, plannedFocus) = FocusRequester.createRefs()

    LaunchedEffect(itemDetails.id, Unit) {
        if (itemDetails.id == null)
            descField.requestFocus()
        else if (itemDetails.isPlanned)
            plannedFocus.requestFocus()
        else
            factFocus.requestFocus()
    }

    Scaffold(
        topBar = { AppBar(
            title = if (itemDetails.id != null) R.string.title_activity_transaction else R.string.title_activity_new_transaction,
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
                value = itemDetails.description,
                singleLine = true,
                label = { Text(stringResource(R.string.activity_transaction_description)) },
                onValueChange = { onValueChange(itemDetails.copy(description = it)) },
                modifier = Modifier.fillMaxWidth(1f)
                    .focusRequester(descField),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions( onNext = { catFocus.requestFocus()})
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall)
            ) {
                OutlinedTextField(
                    value = itemDetails.category,
                    singleLine = true,
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
                    keyboardActions = KeyboardActions( onNext = {
                        if (itemDetails.isPlanned)
                            plannedFocus.requestFocus()
                        else
                            factFocus.requestFocus()
                    })
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
                            .map { it.name }
                            .filter {
                                searchCategory == null || it.lowercase()
                                    .startsWith(searchCategory!!.lowercase())
                            }
                            .sortedBy { it },
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
                    modifier = Modifier.weight(1f)
                        .focusRequester(factFocus),
                    singleLine = true,
                    enabled = !itemDetails.isPlanned,
                    label = { Text(stringResource(id = R.string.activity_transaction_amount_fact)) },
                    value = TextFieldValue(itemDetails.amountFact, selection = TextRange(itemDetails.amountFact.length)),
                    onValueChange = {
                        onValueChange(itemDetails.copy(amountFact = it.text))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                )
                IconButton(
                    enabled = !itemDetails.isPlanned && itemDetails.amountPlanned.isNotEmpty(),
                    onClick = { onValueChange(itemDetails.copy(amountFact = itemDetails.amountPlanned)) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy_back),
                        contentDescription = stringResource(
                            id = R.string.copy_amount
                        )
                    )
                }
                OutlinedTextField(
                    modifier = Modifier.weight(1f)
                        .focusRequester(plannedFocus),
                    singleLine = true,
                    enabled = itemDetails.isPlanned,
                    label = { Text(stringResource(id = R.string.activity_transaction_amount_planned)) },
                    value = TextFieldValue(itemDetails.amountPlanned, selection = TextRange(itemDetails.amountPlanned.length)),
                    onValueChange = {
                        onValueChange(itemDetails.copy(amountPlanned = it.text))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingSmall)
            ) {

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
                                    .height(30.dp)
                                    .padding(horizontal = 0.dp, vertical = 0.dp)
                            )
                            Text(
                                text = text,
                            )
                        }
                    }
                }
                DatePickerButton(
                    millis = itemDetails.date,
                    onChangeDate = {
                        onValueChange(itemDetails.copy(date = it))
                    })
            }

            OutlinedTextField(
                value = itemDetails.note ?: "",
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
                    onClick = {
                        itemDetails.id.let {
                            coroutineScope.launch {
                                viewModel.delete(it)
                                navigateBack()
                            }
                        }
                    },
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

