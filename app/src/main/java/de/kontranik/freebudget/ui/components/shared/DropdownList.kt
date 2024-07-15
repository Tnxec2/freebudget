package de.kontranik.freebudget.ui.components.shared

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import de.kontranik.freebudget.ui.theme.paddingSmall


@Composable
fun DropdownList(
    itemList: List<String>,
    selectedIndex: Int,
    onItemClick: (pos: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    show: Boolean = false,
) {

    var showDropdown by rememberSaveable { mutableStateOf(show) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // button
        Row(
            modifier = modifier
                .clickable { showDropdown = !showDropdown; }
        ) {
            Text(
                text = itemList[selectedIndex], modifier = Modifier
                    .padding(3.dp)
                    .weight(1f)
            )
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "open")
        }

        // dropdown list
        Box {
            if (showDropdown) {
                DropdownListContent(
                    itemList = itemList,
                    scrollState = scrollState,
                    onItemClick = onItemClick,
                    onClose = { showDropdown = false; }
                )
            }
        }
    }
}

@Composable
fun DropdownListContent(
    itemList: List<String>,
    onItemClick: (pos: Int, value: String) -> Unit,
    scrollState: ScrollState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
        ),
        // to dismiss on click outside
        onDismissRequest = { onClose() }
    ) {
        LazyColumn(
            modifier = modifier
                .heightIn(max = 300.dp)
                .padding(paddingSmall)
                //.verticalScroll(state = scrollState)
                .border(width = 1.dp, color = Color.Gray)
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(itemList) { index, item ->
                if (index != 0) {
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            onItemClick(index, item)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(paddingSmall)
                    )
                }
            }

        }
    }
}


