package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler

@Composable
fun CategoryListScreen(
    categoryListState: State<List<Category>>,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    itemViewModel: CategoryItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    fun selectItem(category: Category) {
        itemViewModel.updateUiState(category.toCategoryDetails())
    }

    OrientationChangesHandler(portraitLayout = {
        CategoryListScreenPortrait(
            categoryListState = categoryListState,
            selectItem = { selectItem(it) },
            drawerState = drawerState,
        )
    }, landscapeLayout = {
        CategoryListScreenLandscape(
            categoryListState = categoryListState,
            selectItem = { selectItem(it) },
            drawerState = drawerState,
        )
    })

}