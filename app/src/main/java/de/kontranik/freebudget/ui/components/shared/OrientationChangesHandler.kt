package de.kontranik.freebudget.ui.components.shared

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OrientationChangesHandler(
    portraitLayout: @Composable () -> Unit,
    landscapeLayout: @Composable  () -> Unit,
    modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        landscapeLayout.invoke()
    } else {
        portraitLayout.invoke()
    }
}