package settings.usecase

interface SetLatestDatabaseVersion {
    suspend operator fun invoke(newVersionNumber: Long)
}