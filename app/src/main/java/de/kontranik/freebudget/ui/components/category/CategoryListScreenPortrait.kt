package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.PreviewPortraitLandscapeLightDark
import de.kontranik.freebudget.ui.components.shared.PreviewPortraitLightDark
import de.kontranik.freebudget.ui.theme.AppTheme
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun CategoryListScreenPortrait(
    categoryListState: State<List<Category>>,
    selectItem: (category: Category) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            AppBar(
                titleString = stringResource(id = R.string.title_category),
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
            CategoryList(
                categoryListState = categoryListState,
                selectItem = { selectItem(it)},
                modifier = Modifier.weight(1f)
            )
            CategoryEditForm()
        }
    }
}

@PreviewPortraitLightDark
@Composable
fun CategoryListScreenPotraitPreview() {
    val state = remember {
        mutableStateOf(listOf(
            Category(1, "test", 10.0),
            Category(2, "test1", 200.0),
            Category(3, "test2", 100.0),
            Category(4, "test3", 50.0),
        ))
    }
    AppTheme {
        CategoryListScreenPortrait(
            categoryListState = state,
            selectItem = {},
            drawerState = DrawerState(DrawerValue.Closed)
        )
    }
}