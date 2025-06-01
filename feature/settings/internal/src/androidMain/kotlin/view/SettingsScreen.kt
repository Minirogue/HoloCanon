package view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.holocanon.feature.settings.internal.R
import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.Provider
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.model.DarkModeSetting
import settings.model.Theme
import viewmodel.SettingsViewModel
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "SettingsScreen"

@Composable
internal fun SettingsScreen(
    viewModelProvider: Provider<SettingsViewModel>,
    viewModel: SettingsViewModel = viewModel { viewModelProvider() },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.darkModeSetting?.also {
            DarkModeSettings(it, viewModel::updateDarkModeSetting)
        }
        state.theme?.also {
            ThemeSettings(it, viewModel::updateTheme)
        }

        state.checkboxSettings?.let {
            UserDefinedFilters(
                it,
                viewModel::showNameChangeDialog,
                viewModel::flipIsCheckboxActive,
            )
        }
        state.permanentFilters?.let { IncludedMediaTypes(it, viewModel::setPermanentFilterActive) }
        state.wifiOnly?.let {
            DatabaseSyncSettings(
                wifiOnly = it,
                toggleWifiSetting = viewModel::toggleWifiSetting,
                syncDatabase = viewModel::syncDatabase,
            )
        }
        ExportMediaNotes(
            exportMediaNotes = viewModel::exportMediaNotes,
            importMediaNotes = viewModel::importMediaNotes,
        )
    }

    val checkboxNumberForDialog = state.nameChangeDialogShowing
    val dialogOriginalName = when (checkboxNumberForDialog) {
        1 -> state.checkboxSettings?.checkbox1Setting?.name
        2 -> state.checkboxSettings?.checkbox2Setting?.name
        3 -> state.checkboxSettings?.checkbox3Setting?.name
        else -> null
    }
    if (checkboxNumberForDialog != null && dialogOriginalName != null) {
        CheckboxNameChangeDialog(
            whichBox = checkboxNumberForDialog,
            initialName = dialogOriginalName,
            dismissNameChangeDialog = viewModel::dismissNameChangeDialog,
            setCheckboxName = viewModel::setCheckboxName,
        )
    }
}

@Composable
private fun UserDefinedFilters(
    checkBoxSettings: CheckboxSettings,
    showNameChangeDialog: (Int) -> Unit,
    flipIsCheckboxActive: (Int) -> Unit,
) {
    UserDefinedFilter(
        1,
        checkBoxSettings.checkbox1Setting,
        showNameChangeDialog,
        flipIsCheckboxActive,
    )
    UserDefinedFilter(
        2,
        checkBoxSettings.checkbox2Setting,
        showNameChangeDialog,
        flipIsCheckboxActive,
    )
    UserDefinedFilter(
        3,
        checkBoxSettings.checkbox3Setting,
        showNameChangeDialog,
        flipIsCheckboxActive,
    )
}

@Composable
private fun ThemeSettings(
    currentTheme: Theme,
    updateTheme: (Theme) -> Unit,
) = Card(
    modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
) {
    TabRow(currentTheme.ordinal) {
        Theme.entries.forEach { theme ->
            // Dynamic themes only available on Android 12 and later
            if (theme != Theme.Dynamic || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Tab(
                    selected = theme == currentTheme,
                    onClick = { updateTheme(theme) },
                    text = {
                        Text(
                            when (theme) {
                                Theme.Force -> "Force Theme"
                                Theme.Mace -> "Mace Theme"
                                Theme.Dynamic -> "Dynamic Theme"
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun DarkModeSettings(
    currentDarkModeSetting: DarkModeSetting,
    updateDarkModeSetting: (DarkModeSetting) -> Unit,
) = Card(
    modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
) {
    TabRow(currentDarkModeSetting.ordinal) {
        DarkModeSetting.entries.forEach { darkModeSetting ->
            Tab(
                selected = darkModeSetting == currentDarkModeSetting,
                onClick = { updateDarkModeSetting(darkModeSetting) },
                text = {
                    Text(
                        when (darkModeSetting) {
                            DarkModeSetting.SYSTEM -> stringResource(R.string.settings_system_dark_mode)
                            DarkModeSetting.LIGHT -> stringResource(R.string.settings_light_mode)
                            DarkModeSetting.DARK -> stringResource(R.string.settings_dark_mode)
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun UserDefinedFilter(
    whichBox: Int,
    checkboxSetting: CheckboxSetting,
    showNameChangeDialog: (Int) -> Unit,
    flipIsCheckboxActive: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(
                R.string.settings_user_defined_filter,
                whichBox.toString(),
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
        if (checkboxSetting.isInUse) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNameChangeDialog(whichBox) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.settings_filter_name),
                    modifier = Modifier.padding(8.dp),
                )
                Text(
                    text = checkboxSetting.name,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { flipIsCheckboxActive(whichBox) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_include_filter),
                modifier = Modifier.padding(8.dp),
            )
            Switch(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { flipIsCheckboxActive(whichBox) },
                checked = checkboxSetting.isInUse,
                onCheckedChange = null,
            )
        }
    }
}

@Composable
private fun CheckboxNameChangeDialog(
    whichBox: Int,
    initialName: String,
    dismissNameChangeDialog: () -> Unit,
    setCheckboxName: (Int, String) -> Unit,
) {
    var newName by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = { dismissNameChangeDialog() },
        confirmButton = {
            TextButton(onClick = { setCheckboxName(whichBox, newName) }) {
                Text(text = stringResource(R.string.settings_save))
            }
        },
        dismissButton = {
            TextButton(onClick = { dismissNameChangeDialog() }) {
                Text(text = stringResource(R.string.settings_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.settings_filter_name_change_text,
                    whichBox.toString(),
                ),
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text(stringResource(R.string.settings_new_name)) },
            )
        },
    )
}

@Composable
private fun IncludedMediaTypes(
    permanentFilters: Map<MediaType, Boolean>,
    setPermanentFilterActive: (MediaType, Boolean) -> Unit,
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Text(
            stringResource(R.string.settings_included_media_types),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
        MediaType.entries.forEach {
            MediaTypePermanentFilter(it, permanentFilters[it] ?: true, setPermanentFilterActive)
        }
    }
}

@Composable
private fun MediaTypePermanentFilter(
    mediaType: MediaType,
    isActive: Boolean,
    setPermanentFilterActive: (MediaType, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { setPermanentFilterActive(mediaType, !isActive) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(mediaType.getSerialName())
        Checkbox(
            checked = isActive,
            onCheckedChange = null,
        )
    }
}

@Composable
private fun DatabaseSyncSettings(
    wifiOnly: Boolean,
    toggleWifiSetting: (Boolean) -> Unit,
    syncDatabase: () -> Unit,
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Text(
            stringResource(R.string.settings_sync_settings),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleWifiSetting(!wifiOnly) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_wifi_only_setting),
                modifier = Modifier.padding(8.dp),
            )
            Switch(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { toggleWifiSetting(!wifiOnly) },
                checked = wifiOnly,
                onCheckedChange = null,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { syncDatabase() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.settings_sync_online),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun ExportMediaNotes(
    exportMediaNotes: (OutputStream) -> Unit,
    importMediaNotes: (InputStream) -> Unit,
) {
    val context = LocalContext.current
    val importMediaNotesLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val dataUri = result.data?.data
                if (dataUri != null) {
                    try {
                        val inputStream = context.contentResolver?.openInputStream(dataUri)
                        if (inputStream != null) {
                            importMediaNotes(inputStream)
                        } else {
                            Log.e(TAG, "inputStream not found when importing media notes")
                        }
                    } catch (e: FileNotFoundException) {
                        Log.e(TAG, "Error importing media notes", e)
                    }
                }
            }
        }

    val exportMediaNotesLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val data = result.data?.data
                    if (data != null) {
                        val outputStream = context.contentResolver?.openOutputStream(data)
                        if (outputStream != null) {
                            exportMediaNotes(outputStream)
                        }
                    }
                } catch (e: FileNotFoundException) {
                    Log.e(TAG, "Error exporting media notes", e)
                }
            }
        }

    Card(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(R.string.settings_export_import_user_data),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/json"
                        putExtra(Intent.EXTRA_TITLE, "holocanon_user_data.json")
                    }

                    exportMediaNotesLauncher.launch(intent)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_export_user_data_as_json),
                modifier = Modifier.padding(8.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent().apply {
                        action = Intent.ACTION_GET_CONTENT
                        type = "application/json"
                    }
                    importMediaNotesLauncher.launch(intent)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_import_user_data_from_json),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
