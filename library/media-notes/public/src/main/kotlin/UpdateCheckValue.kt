interface UpdateCheckValue {
    suspend operator fun invoke(checkbox: CheckBoxNumber, mediaItemId: Long, newValue: Boolean)
}