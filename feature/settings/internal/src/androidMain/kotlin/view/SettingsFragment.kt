package view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.holocanon.feature.settings.internal.R
import com.minirogue.common.model.MediaType
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import viewmodel.SettingsState
import viewmodel.SettingsViewModel
import java.io.FileNotFoundException

@AndroidEntryPoint
internal class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()

    val exportMediaNotesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val data = result.data?.data
                    if (data != null) {
                        val outputStream = activity?.contentResolver?.openOutputStream(data)
                        if (outputStream != null) {
                            viewModel.exportMediaNotes(outputStream)
                        }
                    }
                } catch (e: FileNotFoundException) {
                    Log.e(TAG, "Error exporting media notes", e)
                }
            }
        }

    val importMediaNotesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val dataUri = result.data?.data
                if (dataUri != null) {
                    try {
                        val inputStream = activity?.contentResolver?.openInputStream(dataUri)
                        if (inputStream != null) {
                            viewModel.importMediaNotes(inputStream)
                        } else {
                            Log.e(TAG, "inputStream not found when importing media notes")
                        }
                    } catch (e: FileNotFoundException) {
                        Log.e(TAG, "Error importing media notes", e)
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            val state = viewModel.state.collectAsState(SettingsState())
            HolocanonTheme {
                SettingsScreen(state)
            }
        }
    }

    @Composable
    private fun SettingsScreen(state: State<SettingsState>) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.value.checkboxSettings?.let { UserDefinedFilters(it) }
            state.value.permanentFilters?.let { IncludedMediaTypes(it) }
            state.value.wifiOnly?.let { DatabaseSyncSettings(wifiOnly = it) }
            ExportMediaNotes()
        }
        val checkboxNumberForDialog = state.value.nameChangeDialogShowing
        val dialogOriginalName = when (checkboxNumberForDialog) {
            1 -> state.value.checkboxSettings?.checkbox1Setting?.name
            2 -> state.value.checkboxSettings?.checkbox2Setting?.name
            3 -> state.value.checkboxSettings?.checkbox3Setting?.name
            else -> null
        }
        if (checkboxNumberForDialog != null && dialogOriginalName != null) {
            CheckboxNameChangeDialog(
                whichBox = checkboxNumberForDialog,
                initialName = dialogOriginalName,
            )
        }
    }

    @Composable
    private fun UserDefinedFilters(checkBoxSettings: CheckboxSettings) {
        UserDefinedFilter(1, checkBoxSettings.checkbox1Setting)
        UserDefinedFilter(2, checkBoxSettings.checkbox2Setting)
        UserDefinedFilter(3, checkBoxSettings.checkbox3Setting)
    }

    @Composable
    private fun UserDefinedFilter(whichBox: Int, checkboxSetting: CheckboxSetting) {
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
                        .clickable { viewModel.showNameChangeDialog(whichBox) },
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
                    .clickable { viewModel.flipIsCheckboxActive(whichBox) },
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
                        .clickable { viewModel.flipIsCheckboxActive(whichBox) },
                    checked = checkboxSetting.isInUse,
                    onCheckedChange = null,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CheckboxNameChangeDialog(whichBox: Int, initialName: String) {
        var newName by remember { mutableStateOf(initialName) }
        AlertDialog(
            onDismissRequest = { viewModel.dismissNameChangeDialog() },
            confirmButton = {
                TextButton(onClick = { viewModel.setCheckboxName(whichBox, newName) }) {
                    Text(text = stringResource(R.string.settings_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissNameChangeDialog() }) {
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
    private fun IncludedMediaTypes(permanentFilters: Map<MediaType, Boolean>) {
        Card(modifier = Modifier.padding(8.dp)) {
            Text(
                stringResource(R.string.settings_included_media_types),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp),
            )
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
            MediaType.entries.forEach {
                MediaTypePermanentFilter(it, permanentFilters[it] ?: true)
            }
        }
    }

    @Composable
    private fun MediaTypePermanentFilter(mediaType: MediaType, isActive: Boolean) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { viewModel.setPermanentFilterActive(mediaType, !isActive) },
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
    private fun DatabaseSyncSettings(wifiOnly: Boolean) {
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
                    .clickable { viewModel.toggleWifiSetting(!wifiOnly) },
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
                        .clickable { viewModel.toggleWifiSetting(!wifiOnly) },
                    checked = wifiOnly,
                    onCheckedChange = null,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.syncDatabase() },
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
    private fun ExportMediaNotes() {
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
                    .clickable { exportMediaNotes() },
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
                    .clickable { importMediaNotes() },
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

    private fun exportMediaNotes() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "holocanon_user_data.json")
        }

        exportMediaNotesLauncher.launch(intent)
    }

    private fun importMediaNotes() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "application/json"
        }
        importMediaNotesLauncher.launch(intent)
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}
