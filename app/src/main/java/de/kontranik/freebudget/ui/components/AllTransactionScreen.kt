package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.activity.MainActivity
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun AllTransactionScreen(
    mainActivity: MainActivity,
    transactions: State<List<Transaction>>,
    markLastEdited: Boolean,
    lastEditedId: State<Long?>,
    onClickTransaction: (position: Int, item: Transaction) -> Unit,
    onPlanRegularClick: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onAdd,
//                shape = CircleShape,
//            ) {
//                Icon(Icons.Filled.Add, "Floating action button.")
//            }
//        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            MonthSelector(
                year = mainActivity.year,
                month = mainActivity.month,
                onPrev = onPrevMonth,
                onNext = onNextMonth,
                modifier = modifier
            )
            if (transactions.value.isEmpty()) {
                Button(
                    onClick = onPlanRegularClick,
                    colors = ButtonDefaults.buttonColors(containerColor  = colorResource(id = R.color.colorBackground)),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {
                    Text(
                        text = stringResource(id = R.string.put_regular).uppercase(),
                        modifier = modifier)
                }
            }
            AllTransactionList(
                transactions = transactions,
                markLastEdited = markLastEdited,
                lastEditedId = lastEditedId,
                onClick = onClickTransaction)
        }
    }

} 
