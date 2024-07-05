package de.kontranik.freebudget.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Locale

@Composable
fun Amount(amount: Double, modifier: Modifier = Modifier) {
    Text(
        text = String.format(Locale.getDefault(), "%.2f", amount),
        color = if (amount > 0) colorResource(R.color.colorGreen) else if (amount < 0) colorResource(
            id = R.color.colorRed
        ) else colorResource(id = R.color.colorTextListItem),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = modifier
            .padding(start = paddingSmall)
    )
}