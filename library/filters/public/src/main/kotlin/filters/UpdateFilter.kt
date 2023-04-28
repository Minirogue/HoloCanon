package filters

import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType

interface UpdateFilter {
    suspend operator fun invoke(mediaFilter: MediaFilter)
    suspend operator fun invoke(filterType: FilterType)
}
