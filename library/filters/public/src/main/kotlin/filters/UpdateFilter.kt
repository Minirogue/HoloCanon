package filters

interface UpdateFilter {
    suspend operator fun invoke(mediaFilter: MediaFilter)
    suspend operator fun invoke(filterGroup: FilterGroup)
}
