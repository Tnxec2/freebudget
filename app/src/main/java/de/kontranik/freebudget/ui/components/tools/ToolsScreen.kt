package de.kontranik.freebudget.ui.components.tools

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kontranik.freebudget.R
import de.kontranik.freebudget.ui.AppViewModelProvider
import de.kontranik.freebudget.ui.components.appbar.AppBar
import de.kontranik.freebudget.ui.components.shared.ConfirmDialog
import de.kontranik.freebudget.ui.theme.paddingBig
import de.kontranik.freebudget.ui.theme.paddingMedium
import de.kontranik.freebudget.ui.theme.paddingSmall
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun ToolsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    toolsViewModel: ToolsViewModel = viewModel(factory = AppViewModelProvider.Factory),) {

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val openConfirmRestoreDBDialog = remember { mutableStateOf(false) }
    val restoreDBUri = remember { mutableStateOf<Uri?>(null) }

    val filePickerRegularTransactions = rememberLauncherForActivityResult(
        contract = GetFileToOpen(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        toolsViewModel.importFileRegularTransactions(uri, context)
                        snackbarHostState.showSnackbar(context.getString(R.string.importOK_filename, uri.path))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        snackbarHostState.showSnackbar(context.getString(R.string.importFail, e.message))
                    }
                }
            }
        })

    val filePickerNormalTransactions = rememberLauncherForActivityResult(
        contract = GetFileToOpen(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        toolsViewModel.importFileNormalTransaction(uri, context)
                        snackbarHostState.showSnackbar(context.getString(R.string.importOK_filename, uri.path))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        snackbarHostState.showSnackbar(context.getString(R.string.importFail, e.message))
                    }
                }
            }
        })

    val filePickerDatabaseRestore = rememberLauncherForActivityResult(
        contract = GetFileToOpen(),
        onResult = { uri ->
            restoreDBUri.value = uri
            if (uri != null) openConfirmRestoreDBDialog.value = true
        })

    fun exportRegular() {
        scope.launch {
            try {
                val result = toolsViewModel.exportRegular()
                snackbarHostState.showSnackbar(
                    context.getString(
                        R.string.exportOK_filename,
                        result
                    )
                )
            } catch (e: IOException) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.exportFail,
                            e.localizedMessage
                        )
                    )
                }
            }
        }
    }

    fun exportNormal() {
        scope.launch {
            try {
                val result = toolsViewModel.exportNormal()
                snackbarHostState.showSnackbar(context.getString(R.string.exportOK_filename, result))
            } catch (e: IOException) {
                snackbarHostState.showSnackbar(context.getString(R.string.exportFail, e.localizedMessage))
            }
        }
    }

    fun backupDB() {
        scope.launch {
            try {
                val result = toolsViewModel.backupDB(context)
                snackbarHostState.showSnackbar(context.getString(R.string.exportOK_filename, result))
            } catch (e: IOException) {
                snackbarHostState.showSnackbar(context.getString(R.string.exportFail, e.localizedMessage))
            }
        }
    }

    fun importRegular() {
        filePickerRegularTransactions.launch("text/*")
    }
    fun importNormal() {
        filePickerNormalTransactions.launch("text/*")
    }
    fun pickFileForRestoreDB() {
        filePickerDatabaseRestore.launch("*/*")
    }

    fun restoreDB() {
        scope.launch {
            restoreDBUri.value?.let {
                scope.launch {
                    try {
                        toolsViewModel.importDB(it, context)
                        snackbarHostState.showSnackbar(context.getString(R.string.importOK_filename, it.path))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        snackbarHostState.showSnackbar(context.getString(R.string.importFail, e.message))
                    }
                }
            }
            restoreDBUri.value = null
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.import_export,
                drawerState = drawerState) },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                Snackbar(
                    modifier = Modifier.padding(paddingMedium),
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .fillMaxSize()
                .padding(paddingSmall)
                .verticalScroll(
                    rememberScrollState()
                )) {
            Button(
                onClick = { importRegular() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.import_regular).uppercase(),)
            }
            Button(
                onClick = {
                    exportRegular()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.export_regular).uppercase(),)
            }
            Spacer(modifier = Modifier.height(paddingBig))
            Button(
                onClick = { importNormal() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.import_normal).uppercase(),)
            }
            Button(
                onClick = { exportNormal() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.export_normal).uppercase(),)
            }
            Spacer(modifier = Modifier.height(paddingBig))
            Button(
                onClick = { backupDB() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.db_backup).uppercase(),)
            }
            Button(
                onClick = { pickFileForRestoreDB() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.db_restore).uppercase(),)
            }
        }
        when {
            openConfirmRestoreDBDialog.value -> {
                ConfirmDialog(
                    text = stringResource(R.string.confirm_restor_db_text),
                    onDismissRequest = { openConfirmRestoreDBDialog.value = false },
                    onConfirmation = {
                        openConfirmRestoreDBDialog.value = false
                        restoreDB()
                    }
                )
            }
        }
    }
}

