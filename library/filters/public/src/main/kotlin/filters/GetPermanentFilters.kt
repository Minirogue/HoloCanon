package filters

interface GetPermanentFilters {
    suspend operator fun invoke(): List<MediaFilter>
}
