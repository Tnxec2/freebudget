package de.kontranik.freebudget.ui.components.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Locale

@Composable
fun OverviewCategorySummary(
    categoryList: MutableMap<String, Category>,
    onSelect: (categoryName: String) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    ) {

    val maxCategoryWeight = categoryList.values.maxOfOrNull { it.weight }

    LazyColumn(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall)) {
        itemsIndexed(categoryList.values.sortedByDescending { it.weight }.toList()) { index, category ->
            CategoryRow(
                category,
                maxCategoryWeight = maxCategoryWeight ?: 0.0,
                onClick = onSelect,
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
        }
    }
}

@Composable
fun CategoryRow(
    category: Category,
    maxCategoryWeight: Double,
    onClick: (name: String)-> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(category.name) }
            .padding(vertical = paddingSmall / 2)) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.inversePrimary,
            trackColor = Color.Transparent,
            progress = { (category.weight / maxCategoryWeight).toFloat() },
            modifier = Modifier
                .fillMaxSize()
                .height(30.dp)
        )
        Row(Modifier.fillMaxHeight()) {
            Text(text = category.name,
                Modifier
                    .weight(1f)
                    .padding(start = paddingSmall))
            Text(text = String.format(Locale.getDefault(), "%.2f", category.weight), modifier = Modifier.padding(end = paddingSmall))
        }
    }
}

@PreviewLightDark
@Composable
private fun OverviewCategorySummaryPreview() {
    AppTheme {
        OverviewCategorySummary(categoryList = mutableMapOf(
            "Test" to Category(0, "test", 10.0),
            "Test1" to Category(0, "test1", 200.0),
            "Test2" to Category(0, "test2", 100.0),
            "Test3" to Category(0, "test3", 50.0),
        ), onSelect = {})
    }
}