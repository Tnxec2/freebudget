package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun CategoryList(
    categoryListState: State<List<Category>>,
    selectItem: (category: Category) -> Unit,
    modifier: Modifier = Modifier) {

    LazyColumn(modifier = modifier) {
        itemsIndexed(categoryListState.value.distinctBy { it.name }) { index, item ->
            if (index != 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(paddingSmall)
                .clickable { selectItem(item) }) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CategoryListPreview() {
    val state = remember {
        mutableStateOf(listOf(
            Category(1, "test", 10.0),
            Category(2, "test1", 200.0),
            Category(3, "test2", 100.0),
            Category(4, "test3", 50.0),
        ))
    }
    AppTheme {
        CategoryList(categoryListState = state, selectItem = {})
    }

}