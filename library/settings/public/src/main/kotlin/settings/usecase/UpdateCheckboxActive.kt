package settings.usecase

interface UpdateCheckboxActive {
    suspend operator fun invoke(whichBox: Int, newValue: Boolean)
}
