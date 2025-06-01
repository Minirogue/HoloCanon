package filters

import com.minirogue.starwarscanontracker.core.data.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.data.entity.FilterTypeDto
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.model.FilterGroup
import filters.model.MediaFilter

@Inject
@ContributesBinding(AppScope::class)
class UpdateFilterImpl(private val daoFilter: DaoFilter) : UpdateFilter {
    /**
     * Persist a FilterObject to the database.
     */
    override suspend operator fun invoke(mediaFilter: MediaFilter) = daoFilter.update(mediaFilter.toFilterObject())

    /**
     * Persist a filters.FilterType to the database.
     */
    override suspend operator fun invoke(filterGroup: FilterGroup) = daoFilter.update(filterGroup.toLocalDto())

    private fun FilterGroup.toLocalDto(): FilterTypeDto = FilterTypeDto(
        typeId = type.legacyIntegerConversion,
        isFilterPositive = isFilterPositive,
        text = text,
    )
}
