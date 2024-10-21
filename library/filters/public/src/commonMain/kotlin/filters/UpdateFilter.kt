package filters

import filters.model.FilterGroup
import filters.model.MediaFilter

interface UpdateFilter {
    suspend operator fun invoke(mediaFilter: MediaFilter)
    suspend operator fun invoke(filterGroup: FilterGroup)
}
