package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs


@Composable
fun swipableModifier(
    modifier: Modifier,
    onLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onDown: () -> Unit = {},
    onUp: () -> Unit = {}
): Modifier {
    var dragDirection by remember { mutableIntStateOf(-1) }

    return modifier
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    if (abs(x) > abs(y)) {
                        when {
                            x > 0 -> {
                                dragDirection = 0
                            } // right
                            x < 0 -> {
                                dragDirection = 1
                            } // left
                        }
                    } else {
                        when {
                            y > 0 -> {
                                dragDirection = 2
                            } // down
                            y < 0 -> {
                                dragDirection = 3
                            } // top
                        }
                    }
                },
                onDragEnd = {
                    when (dragDirection) {
                        0 -> onLeft()
                        1 -> onRight()
                        2 -> onDown()
                        3 -> onUp()
                    }
                }
            )
        }
}