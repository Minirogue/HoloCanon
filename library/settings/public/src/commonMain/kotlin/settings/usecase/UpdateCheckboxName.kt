package settings.usecase

interface UpdateCheckboxName {
    suspend operator fun invoke(whichBox: Int, newName: String)
}
