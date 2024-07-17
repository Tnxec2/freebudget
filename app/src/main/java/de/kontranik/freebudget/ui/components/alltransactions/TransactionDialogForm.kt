package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.AmountTypeSelector
import de.kontranik.freebudget.ui.components.shared.CategorySelectorBox
import de.kontranik.freebudget.ui.components.shared.DatePickerButton
import de.kontranik.freebudget.ui.theme.paddingSmall

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransactionDialogForm(
    itemDetails: TransactionItemDetails,
    onValueChange: (item: TransactionItemDetails) -> Unit,
    modifier: Modifier = Modifier,
) {

    val (descField, catFocus, factFocus, plannedFocus) = createRefs()

    LaunchedEffect(itemDetails.id, Unit) {
        if (itemDetails.id == null)
            descField.requestFocus()
        else if (itemDetails.isPlanned)
            plannedFocus.requestFocus()
        else
            factFocus.requestFocus()
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
            value = itemDetails.description,
            singleLine = true,
            label = { Text(stringResource(R.string.activity_transaction_description)) },
            onValueChange = { onValueChange(itemDetails.copy(description = it)) },
            modifier = Modifier
                .fillMaxWidth(1f)
                .focusRequester(descField),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { catFocus.requestFocus() })
        )
        CategorySelectorBox(
            category = itemDetails.category,
            onValueChange = { onValueChange(itemDetails.copy(category = it)) },
            focusRequester = catFocus,
            nextFocusRequester = if (itemDetails.isPlanned)
                plannedFocus
            else
                factFocus)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(factFocus),
                singleLine = true,
                enabled = !itemDetails.isPlanned,
                label = { Text(stringResource(id = R.string.activity_transaction_amount_fact)) },
                value = TextFieldValue(
                    itemDetails.amountFact,
                    selection = TextRange(itemDetails.amountFact.length)
                ),
                onValueChange = {
                    onValueChange(itemDetails.copy(amountFact = it.text))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
            )
            IconButton(
                enabled = itemDetails.canCopy(),
                onClick = { onValueChange(itemDetails.copy(amountFact = itemDetails.amountPlanned)) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy_back),
                    contentDescription = stringResource(
                        id = R.string.copy_amount
                    )
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(plannedFocus),
                singleLine = true,
                enabled = itemDetails.isPlanned,
                label = { Text(stringResource(id = R.string.activity_transaction_amount_planned)) },
                value = TextFieldValue(
                    itemDetails.amountPlanned,
                    selection = TextRange(itemDetails.amountPlanned.length)
                ),
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
        ) {
            AmountTypeSelector(
                isIncome = itemDetails.isIncome,
                onValueChange = { onValueChange(itemDetails.copy(isIncome = it)) })
            Spacer(modifier = Modifier.weight(1f))
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

    }
}