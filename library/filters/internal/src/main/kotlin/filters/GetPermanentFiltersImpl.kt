package filters

import com.minirogue.common.model.MediaType
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import settings.usecase.GetPermanentFilterSettings
import javax.inject.Inject

class GetPermanentFiltersImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilterSettings: GetPermanentFilterSettings,
) : GetPermanentFilters {
    override suspend operator fun invoke(): List<MediaFilter> = withContext(Dispatchers.IO) {
        val filterList = ArrayList<MediaFilter>()
        val permanentFilters = getPermanentFilterSettings().first()
        for (type in MediaType.entries) {
            if (permanentFilters[type] == false) {
                daoFilter.getFilter(type.legacyId, FilterTypeDto.FILTERCOLUMN_TYPE)?.toMediaFilter()
                    ?.let { filterList.add(it) }
            }
        }
        filterList
    }
}
