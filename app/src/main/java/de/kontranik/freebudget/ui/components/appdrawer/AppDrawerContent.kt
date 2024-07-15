package de.kontranik.freebudget.ui.components.appdrawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@Composable
fun <T : Enum<T>> AppDrawerContent(
    drawerState: DrawerState,
    menuItems: List<AppDrawerItemInfo<T>>,
    defaultPick: T,
    onClick: (T) -> Unit
) {
    //var currentPick by remember { mutableStateOf(defaultPick) }
    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingMedium),
                ) {
                    items(menuItems) { item ->
                        AppDrawerItem(item = item) { navOption ->

//                            if (currentPick == navOption) {
//                                coroutineScope.launch {
//                                    drawerState.close()
//                                }
//                                return@AppDrawerItem
//                            }

                            //currentPick = navOption
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            onClick(navOption)
                        }
                    }
                }

        }
    }
}