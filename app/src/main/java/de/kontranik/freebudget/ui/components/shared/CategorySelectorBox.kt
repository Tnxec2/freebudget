package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun CategorySelectorBox(
    category: String,
    onValueChange: (String) -> Unit, // onValueChange(itemDetails.copy(category = it))
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester(),
    nextFocusRequester: FocusRequester = FocusRequester(),
    categoryViewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ) {

    val categorys = categoryViewModel.distinctCategoryNames.observeAsState(listOf())

    var showCategoryDropdown by rememberSaveable {
        mutableStateOf(false)
    }
    var searchCategory by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val scrollStateCategory = rememberScrollState()

    Column( modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingSmall)
        ) {
            OutlinedTextField(
                singleLine = true,
                value = category,
                label = { Text(stringResource(R.string.title_category)) },
                onValueChange = {
                    onValueChange(it)
                    searchCategory = it
                    showCategoryDropdown = it.isNotEmpty()
                },
                modifier = Modifier.weight(1f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions( onNext = {nextFocusRequester.requestFocus()})
            )
            IconButton(
                onClick = {
                    if (showCategoryDropdown.not()) searchCategory = null
                    showCategoryDropdown = showCategoryDropdown.not()
                }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "open select menu")
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
                    onItemClick = { _, value -> onValueChange(value) },
                    onClose = { showCategoryDropdown = false; }
                )
            }
        }
    }
}