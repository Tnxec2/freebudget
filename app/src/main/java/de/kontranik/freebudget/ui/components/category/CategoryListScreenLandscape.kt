package de.kontranik.freebudget.ui.components.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.kontranik.freebudget.R
import de.kontranik.freebudget.model.Category
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.theme.paddingSmall

@Composable
fun CategoryListScreenLandscape(
    categoryListState: State<List<Category>>,
    selectItem: (category: Category) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {

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
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(paddingSmall)
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                CategoryList(categoryListState = categoryListState, selectItem = { selectItem(it) })
            }
            Spacer(modifier = Modifier.width(paddingSmall))
            Column(
                Modifier.weight(1f)
            ) {
                CategoryEditForm()
            }
        }
    }
}