package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.AmountTypeSelector
import de.kontranik.freebudget.ui.components.shared.CategorySelectorBox
import de.kontranik.freebudget.ui.components.shared.DatePickerButton
import de.kontranik.freebudget.ui.components.shared.DropdownList
import de.kontranik.freebudget.ui.helpers.DateUtils
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingSmall


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegularTransactionDialogForm(
    itemDetails: RegularTransactionItem,
    onValueChange: (item: RegularTransactionItem) -> Unit,
    modifier: Modifier = Modifier,
) {

    val (descFocus, catFocus, dayFocus, amountFocus) = FocusRequester.createRefs()

    LaunchedEffect(itemDetails.id, Unit) {
        if (itemDetails.id == null)
            descFocus.requestFocus()
        else
            amountFocus.requestFocus()
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                singleLine = true,
                value = itemDetails.description,
                label = { Text(stringResource(R.string.activity_transaction_description)) },
                onValueChange = { onValueChange(itemDetails.copy(description = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descFocus),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions( onNext = {catFocus.requestFocus()})
            )
            CategorySelectorBox(
                category = itemDetails.category,
                onValueChange = { onValueChange(itemDetails.copy(category = it)) },
                focusRequester = catFocus,
                nextFocusRequester = amountFocus)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(amountFocus),
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
                AmountTypeSelector(isIncome = itemDetails.isIncome, onValueChange = { onValueChange(itemDetails.copy(isIncome = it)) })
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.start_date),
                    modifier = Modifier.weight(1f))
                DatePickerButton(
                    millis = itemDetails.dateStart,
                    onChangeDate = {
                        onValueChange(itemDetails.copy(dateStart = it)) },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    enabled = itemDetails.dateStart != null,
                    onClick = {
                    onValueChange(itemDetails.copy(dateStart = null))
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "clear date")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.end_date),
                    modifier = Modifier.weight(1f))
                DatePickerButton(
                    millis = itemDetails.dateEnd,
                    onChangeDate = {
                        onValueChange(itemDetails.copy(dateEnd = it))
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    enabled = itemDetails.dateEnd != null,
                    onClick = {
                    onValueChange(itemDetails.copy(dateEnd = null))
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "clear date")
                }
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
    }
}

@Preview
@Composable
private fun RegularTransactionDialogFormPreview() {
    val item = RegularTransactionItem().copy(
        day = "15",
        month = 2,
        description = "Description",
        category = "Category",
        amount = "292.20",
        dateStart = DateUtils.now(),
        dateEnd = DateUtils.now(),
        note = "This is a note"
    )
    AppTheme {
        RegularTransactionDialogForm(itemDetails = item, onValueChange = {})
    }

}
