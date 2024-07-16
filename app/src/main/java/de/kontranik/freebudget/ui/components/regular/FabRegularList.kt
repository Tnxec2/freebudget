package de.kontranik.freebudget.ui.components.regular

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.components.alltransactions.FabNormalList
import de.kontranik.freebudget.ui.components.shared.FabItem
import de.kontranik.freebudget.ui.components.shared.FabTransactionItem
import de.kontranik.freebudget.ui.components.shared.TransactionType
import de.kontranik.freebudget.ui.theme.color_green2
import de.kontranik.freebudget.ui.theme.color_red2
import de.kontranik.freebudget.ui.theme.paddingBig


@Composable
fun FabRegularList(
    onAdd: (type: TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {

    fun onClick(type: TransactionType) {

        onAdd(type)
    }


    Row {
        FabTransactionItem(
            fabItem = FabItem(
                { onClick(TransactionType.INCOME_REGULAR) },
                color_green2,
                R.drawable.ic_baseline_add_24,
                R.string.activity_main_receipts_planned
            )
        )
        Spacer(Modifier.width(paddingBig))


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

@Preview
@Composable
private fun FabPreview() {
    FabNormalList(
        onAdd = { }
    )

}


