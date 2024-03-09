package settings.model

data class CheckboxSettings(
        val checkbox1Setting: CheckboxSetting,
        val checkbox2Setting: CheckboxSetting,
        val checkbox3Setting: CheckboxSetting
) {
    companion object {
        val NONE = CheckboxSettings(
                CheckboxSetting(null, false),
                CheckboxSetting(null, false),
                CheckboxSetting(null, false)
        )
    }
}

data class CheckboxSetting(val name: String?, val isInUse: Boolean)
