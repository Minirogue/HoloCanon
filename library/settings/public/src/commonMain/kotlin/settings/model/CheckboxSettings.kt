package settings.model

data class CheckboxSettings(
        val checkbox1Setting: CheckboxSetting,
        val checkbox2Setting: CheckboxSetting,
        val checkbox3Setting: CheckboxSetting
)

data class CheckboxSetting(val name: String, val isInUse: Boolean)
