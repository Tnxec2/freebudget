package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun FabTransactionItem(fabItem: FabItem, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = { fabItem.onClick() },
        shape = CircleShape,
        containerColor = fabItem.color,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = fabItem.drawable),
            stringResource(id = fabItem.description)
        )
    }
}

class FabItem(val onClick: () -> Unit, val color: Color, val drawable: Int, val description: Int)
