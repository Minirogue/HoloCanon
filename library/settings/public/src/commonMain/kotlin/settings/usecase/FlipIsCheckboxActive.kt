package settings.usecase

interface FlipIsCheckboxActive {
    suspend operator fun invoke(whichBox: Int)
}
