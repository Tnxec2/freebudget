package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.database.viewmodel.CategoryViewModel
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@Composable
fun CategoryListScreen(
    drawerState: DrawerState,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    itemViewModel: CategoryItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val categoryList = viewModel.mAllCategorys.observeAsState(listOf())

    fun selectItem(category: Category) {
        itemViewModel.updateUiState(category.toCategoryDetails())
    }

    Scaffold(
        topBar = {
            AppBar(
                titleString = stringResource(id = R.string.activity_category),
                drawerState = drawerState,
            )
        },
        modifier = modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(paddingSmall)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(categoryList.value) { index, item ->
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
            CategoryEditForm()
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            itemViewModel.updateUiState(CategoryDetails())
                            navigateUp()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        }
    }
}