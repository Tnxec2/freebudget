package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Locale

@Composable
fun Amount(amount: Double,
           modifier: Modifier = Modifier,
           textAlign: TextAlign = TextAlign.Center) {
    Text(
        text = String.format(Locale.getDefault(), "%.2f", if (amount==0.0) 0.0 else amount),
        textAlign = textAlign,
        color = if (amount > 0)
            colorResource(R.color.colorGreen)
        else if (amount < 0)
            colorResource(id = R.color.colorRed)
        else
            colorResource(id = R.color.colorTextListItem),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .padding(start = paddingSmall)
    )
}