package de.kontranik.freebudget.ui.components.category

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.shared.OrientationChangesHandler
import de.kontranik.freebudget.ui.components.shared.PreviewPortraitLandscapeLightDark
import de.kontranik.freebudget.ui.theme.AppTheme

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

