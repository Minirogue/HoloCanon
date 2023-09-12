package settings.model

import com.minirogue.api.media.MediaType

data class AllSettings(
    val checkboxSettings: CheckboxSettings,
    val syncWifiOnly: Boolean,
    val permanentFilterSettings: Map<MediaType, Boolean>,
    val latestDatabaseVersion: Long,
)
