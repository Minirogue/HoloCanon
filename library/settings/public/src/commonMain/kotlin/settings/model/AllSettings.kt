package settings.model

import com.minirogue.common.model.MediaType

data class AllSettings(
    val checkboxSettings: CheckboxSettings,
    val syncWifiOnly: Boolean,
    val permanentFilterSettings: Map<MediaType, Boolean>,
    val latestDatabaseVersion: Long,
    val darkModeSetting: DarkModeSetting,
    val theme: Theme,
)
