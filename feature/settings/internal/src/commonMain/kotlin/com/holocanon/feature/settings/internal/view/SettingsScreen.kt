package com.holocanon.feature.settings.internal.view

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.holocanon.feature.settings.internal.viewmodel.SettingsViewModel
import com.holocanon.library.file.picker.compose.prepareFilePicker
import com.holocanon.library.file.picker.model.FileType
import com.holocanon.library.file.picker.model.PickerArgs
import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.Provider
import holocanon.feature.settings.internal.generated.resources.Res
import holocanon.feature.settings.internal.generated.resources.settings_cancel
import holocanon.feature.settings.internal.generated.resources.settings_dark_mode
import holocanon.feature.settings.internal.generated.resources.settings_dynamic_theme
import holocanon.feature.settings.internal.generated.resources.settings_export_failed
import holocanon.feature.settings.internal.generated.resources.settings_export_import_user_data
import holocanon.feature.settings.internal.generated.resources.settings_export_user_data_as_json
import holocanon.feature.settings.internal.generated.resources.settings_filter_name
import holocanon.feature.settings.internal.generated.resources.settings_filter_name_change_text
import holocanon.feature.settings.internal.generated.resources.settings_force_theme
import holocanon.feature.settings.internal.generated.resources.settings_import_failed
import holocanon.feature.settings.internal.generated.resources.settings_import_user_data_from_json
import holocanon.feature.settings.internal.generated.resources.settings_include_filter
import holocanon.feature.settings.internal.generated.resources.settings_included_media_types
import holocanon.feature.settings.internal.generated.resources.settings_light_mode
import holocanon.feature.settings.internal.generated.resources.settings_mace_theme
import holocanon.feature.settings.internal.generated.resources.settings_new_name
import holocanon.feature.settings.internal.generated.resources.settings_save
import holocanon.feature.settings.internal.generated.resources.settings_sync_online
import holocanon.feature.settings.internal.generated.resources.settings_sync_settings
import holocanon.feature.settings.internal.generated.resources.settings_system_dark_mode
import holocanon.feature.settings.internal.generated.resources.settings_user_defined_filter
import holocanon.feature.settings.internal.generated.resources.settings_wifi_only_setting
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import org.jetbrains.compose.resources.stringResource
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.model.DarkModeSetting
import settings.model.Theme

@Composable
internal fun SettingsScreen(
    viewModelProvider: Provider<SettingsViewModel>,
    viewModel: SettingsViewModel = viewModel { viewModelProvider() },
) {
    val state by viewModel.state.collectAsState()

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
            onError = viewModel::onFileActionFailed,
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
            // TODO replace false with Build.VERSION.SDK_INT >= Build.VERSION_CODES.S and Android
            if (theme != Theme.AndroidDynamic || false) {
                Tab(
                    selected = theme == currentTheme,
                    onClick = { updateTheme(theme) },
                    text = {
                        Text(
                            when (theme) {
                                Theme.Force -> stringResource(Res.string.settings_force_theme)
                                Theme.Mace -> stringResource(Res.string.settings_mace_theme)
                                Theme.AndroidDynamic -> stringResource(Res.string.settings_dynamic_theme)
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
                            DarkModeSetting.SYSTEM -> stringResource(Res.string.settings_system_dark_mode)
                            DarkModeSetting.LIGHT -> stringResource(Res.string.settings_light_mode)
                            DarkModeSetting.DARK -> stringResource(Res.string.settings_dark_mode)
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
                Res.string.settings_user_defined_filter,
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
                    text = stringResource(Res.string.settings_filter_name),
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
                text = stringResource(Res.string.settings_include_filter),
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
                Text(text = stringResource(Res.string.settings_save))
            }
        },
        dismissButton = {
            TextButton(onClick = { dismissNameChangeDialog() }) {
                Text(text = stringResource(Res.string.settings_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(
                    Res.string.settings_filter_name_change_text,
                    whichBox.toString(),
                ),
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text(stringResource(Res.string.settings_new_name)) },
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
            stringResource(Res.string.settings_included_media_types),
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
            stringResource(Res.string.settings_sync_settings),
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
                text = stringResource(Res.string.settings_wifi_only_setting),
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
                stringResource(Res.string.settings_sync_online),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun ExportMediaNotes(
    exportMediaNotes: (RawSink) -> Unit,
    importMediaNotes: (RawSource) -> Unit,
    onError: (String) -> Unit,
) {
    val importExceptionString = stringResource(Res.string.settings_import_failed)
    val exportExceptionString = stringResource(Res.string.settings_export_failed)
    val importLauncher = prepareFilePicker(
        pickerArgs = PickerArgs.Get(FileType.JSON, importMediaNotes),
        onError = { onError(importExceptionString) },
        onCancelled = { },
    )
    val exportLauncher = prepareFilePicker(
        pickerArgs = PickerArgs.Save(
            FileType.JSON,
            defaultFileName = "holocanon_user_data.json",
            exportMediaNotes,
        ),
        onError = { onError(exportExceptionString) },
        onCancelled = { },
    )

    Card(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(Res.string.settings_export_import_user_data),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { exportLauncher.launch() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings_export_user_data_as_json),
                modifier = Modifier.padding(8.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { importLauncher.launch() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings_import_user_data_from_json),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
