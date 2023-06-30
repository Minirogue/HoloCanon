package filters

import android.content.SharedPreferences
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPermanentFiltersImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val sharedPreferences: SharedPreferences,
) : GetPermanentFilters {
    override suspend operator fun invoke(): List<MediaFilter> = withContext(Dispatchers.IO) {
        val filterList = ArrayList<MediaFilter>()
        for (type in MediaType.values()) {
            if (!sharedPreferences.getBoolean(type.getSerialname(), true)) {
                daoFilter.getFilter(type.legacyId, FilterType.FILTERCOLUMN_TYPE)?.toMediaFilter()?.let { filterList.add(it) }
            }
        }
        filterList
    }
}
