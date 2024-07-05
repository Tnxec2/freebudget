package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.model.RegularTransaction
import de.kontranik.freebudget.ui.theme.paddingMedium
import java.util.Calendar

@Composable
fun RegularTransactionScreen(
    month: State<Int?>,
    transactions: State<List<RegularTransaction>>,
    onClickTransaction: (position: Int, item: RegularTransaction) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var income = 0.0
    var bills = 0.0

    for (transaction in transactions.value) {
        if (transaction.isDateInScope(Calendar.getInstance().timeInMillis)) {
            if (transaction.amount > 0) {
                income += transaction.amount
            } else {
                bills += transaction.amount
            }
        }
    }
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
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            month.value?.let {
                MonthSelector(
                    month = it,
                    onPrev = onPrevMonth,
                    onNext = onNextMonth,
                    modifier = modifier
                )
            }
            RegularTransactionSummary(
                income = income,
                bills = bills,
                modifier)
            Box(modifier = Modifier.padding(bottom = paddingMedium))
            RegularTransactionList(
                transactions = transactions,
                onClick = onClickTransaction,
                modifier)
        }
    }

}