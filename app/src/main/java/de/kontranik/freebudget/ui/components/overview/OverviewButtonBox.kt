package de.kontranik.freebudget.ui.components.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun OverviewButtonBox(
    onClickAllTransactionsSeparated: ()-> Unit,
    onClickAllTransactions: ()-> Unit,
    onClickRegularTransactions: ()-> Unit,
    modifier: Modifier = Modifier) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall)) {
        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = onClickAllTransactionsSeparated,
                Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.all_transactions_separated).uppercase(), textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth().padding(bottom = paddingSmall).height(intrinsicSize = IntrinsicSize.Max)) {
            Button(
                onClick = onClickAllTransactions,
                Modifier.weight(1f).fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.all_transactions).uppercase(), textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.width(paddingSmall))
            Button(
                onClick = onClickRegularTransactions,
                Modifier.weight(1f).fillMaxHeight()
            ) {
                Text(text = stringResource(id = R.string.regular).uppercase(), textAlign = TextAlign.Center)
            }
        }
    }
}