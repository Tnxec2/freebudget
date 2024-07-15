package de.kontranik.freebudget.ui.components.alltransactions

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.shared.FabItem
import de.kontranik.freebudget.ui.components.shared.FabTransactionItem
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.color_accent
import de.kontranik.freebudget.ui.theme.color_green
import de.kontranik.freebudget.ui.theme.color_green2
import de.kontranik.freebudget.ui.theme.color_red
import de.kontranik.freebudget.ui.theme.color_red2
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun FabNormalList(
    onAdd: (type: TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showButtons by remember {
        mutableStateOf(false)
    }

    fun toggleButtons() {
        showButtons = showButtons.not()
    }

    fun onClick(type: TransactionType) {
        showButtons = false
        onAdd(type)
    }

    Row {
        if (showButtons) {
            FabTransactionItem(
                fabItem = FabItem(
                    { onClick(TransactionType.INCOME_REGULAR) },
                    color_green2,
                    R.drawable.ic_baseline_add_24,
                    R.string.activity_main_receipts_planned
                )
            )

            Spacer(Modifier.width(paddingSmall))
            if (showButtons) FabTransactionItem(
                fabItem = FabItem(
                    { onClick(TransactionType.INCOME) },
                    color_green,
                    R.drawable.ic_baseline_add_24,
                    R.string.activity_main_receipts_unplanned
                )
            )
            Spacer(Modifier.width(paddingSmall))
        }

        FabTransactionItem(
            fabItem = FabItem(
                { toggleButtons() },
                color_accent,
                R.drawable.ic_baseline_euro_symbol_24,
                R.string.activity_main_receipts_unplanned
            ),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {

                        },
                        onDrag = { pi, offset ->

                        },
                        onDragEnd = {

                        },
                        onDragCancel = {

                        }
                    )
                }
        )

        if (showButtons) {
            Spacer(Modifier.width(paddingSmall))
            FabTransactionItem(
                fabItem = FabItem(
                    { onClick(TransactionType.BILLS) },
                    color_red,
                    R.drawable.ic_baseline_remove_24,
                    R.string.activity_main_spending_unplanned
                )
            )
            Spacer(Modifier.width(paddingSmall))
            FabTransactionItem(
                fabItem = FabItem(
                    { onClick(TransactionType.BILLS_REGULAR) },
                    color_red2,
                    R.drawable.ic_baseline_remove_24,
                    R.string.activity_main_spending_planned
                )
            )
        }
    }
}

@Preview
@Composable
private fun FabPreview() {
    FabNormalList(
        onAdd = { }
    )

}


