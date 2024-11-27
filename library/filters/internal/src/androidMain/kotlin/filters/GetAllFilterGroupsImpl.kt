package filters

import android.util.Log
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
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
        val adaptedFilters = filters.mapNotNull { it.toMediaFilter() }
        filterTypes
            .mapNotNull { it.toFilterGroup() }
            .filter {
                if (it.type == FilterType.Checkbox) {
                    // Only include checkbox group if at least one checkbox is in use
                    checkBoxSettings.checkbox1Setting.isInUse ||
                            checkBoxSettings.checkbox2Setting.isInUse ||
                            checkBoxSettings.checkbox3Setting.isInUse
                } else true
            }
            .associateWith { filterGroup ->
                adaptedFilters.filter { filterGroup.type == it.filterType && it !in permanentFilters }
            }
    }

    private fun FilterTypeDto.toFilterGroup(): FilterGroup? =
        try {
            FilterGroup(
                type = getTypeFromInt(typeId),
                isFilterPositive = isFilterPositive,
                text = text
            )
        } catch (e: IllegalStateException) {
            Log.w("GetAllFilterGroupsImpl", "error adapting filter group", e)
            null
        }

    private fun getTypeFromInt(typeId: Int): FilterType =
        FilterType.values().find { it.legacyIntegerConversion == typeId }
            ?: error("$typeId is not a valid filter type id")
}
