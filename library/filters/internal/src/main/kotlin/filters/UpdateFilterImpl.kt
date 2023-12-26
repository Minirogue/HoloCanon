package filters

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import javax.inject.Inject

internal class UpdateFilterImpl @Inject constructor(private val daoFilter: DaoFilter) : UpdateFilter {
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
