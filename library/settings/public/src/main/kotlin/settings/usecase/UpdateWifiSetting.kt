package settings.usecase

interface UpdateWifiSetting {
    suspend operator fun invoke(newValue: Boolean)
}