package filters

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_ONE
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_THREE
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_TWO
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_PUBLISHER
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_SERIES
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_TYPE
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

internal class GetAllFilterGroupsImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
    private val getCheckboxSettings: GetCheckboxSettings,
) : GetAllFilterGroups {

    override fun invoke(): Flow<Map<FilterGroup, List<MediaFilter>>> = combine(
        daoFilter.getAllFilterTypes(),
        daoFilter.getAllFilters(),
        getPermanentFilters(),
        getCheckboxSettings(),
    ) { filterTypes, filters, permanentFilters, checkBoxSettings ->
        val adaptedFilters = filters.map { it.toMediaFilter() }
        filterTypes
            .map { it.toFilterGroup() }
            .filter {
                when (it.type) {
                    FilterType.CheckboxOne -> checkBoxSettings.checkbox1Setting.isInUse
                    FilterType.CheckboxTwo -> checkBoxSettings.checkbox2Setting.isInUse
                    FilterType.CheckboxThree -> checkBoxSettings.checkbox3Setting.isInUse
                    else -> true
                }
            }
            .associateWith { filterGroup ->
                adaptedFilters.filter { filterGroup.type == it.filterType && it !in permanentFilters }
            }
    }

    private fun FilterTypeDto.toFilterGroup(): FilterGroup =
        FilterGroup(type = getTypeFromInt(typeId), isFilterPositive = isFilterPositive, text = text)

    private fun getTypeFromInt(typeId: Int): FilterType = when (typeId) {
        FILTERCOLUMN_TYPE -> FilterType.MediaType
        FILTERCOLUMN_CHECKBOX_ONE -> FilterType.CheckboxOne
        FILTERCOLUMN_CHECKBOX_TWO -> FilterType.CheckboxTwo
        FILTERCOLUMN_CHECKBOX_THREE -> FilterType.CheckboxThree
        FILTERCOLUMN_SERIES -> FilterType.Series
        FILTERCOLUMN_PUBLISHER -> FilterType.Publisher
        else -> throw IllegalArgumentException("$typeId is not a valid filter type id")
    }
}
