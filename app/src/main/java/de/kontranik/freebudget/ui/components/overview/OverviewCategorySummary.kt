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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.model.Transaction
import de.kontranik.freebudget.ui.theme.paddingSmall
import java.util.Locale
import kotlin.math.abs

@Composable
fun OverviewCategorySummary(
    transactions: List<Transaction>,
    onSelect: (categoryName: String) -> Unit,
    modifier: Modifier = Modifier
    ) {

    val categoryList = mutableMapOf<String, Category>()
    var maxCategoryWeight = 0.0
    val notDefined = stringResource(R.string.activity_transaction_not_define)

    transactions.forEach { transaction ->
        var categoryName = transaction.category.trim()
        if (categoryName.isEmpty()) categoryName = notDefined
        if (transaction.amountFact < 0) {
            if (categoryList.containsKey(categoryName)) {
                categoryList[categoryName]?.weight?.plus(abs(transaction.amountFact))
            } else {
                categoryList[categoryName] = Category(0, categoryName, abs(transaction.amountFact))
            }
            categoryList[categoryName]?.let {
                if (it.weight > maxCategoryWeight) maxCategoryWeight = it.weight
            }
        }
    }

    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall)) {
        itemsIndexed(categoryList.values.sortedByDescending { it.weight }.toList()) { index, category ->
            CategoryRow(
                category,
                maxCategoryWeight = maxCategoryWeight,
                onClick = onSelect,
            )
            if (index < transactions.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
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