package filters

import android.content.SharedPreferences
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPermanentFiltersImpl @Inject constructor(
    private val daoType: DaoType,
    private val daoFilter: DaoFilter,
    private val sharedPreferences: SharedPreferences,
): GetPermanentFilters {
  override  suspend operator fun invoke(): List<MediaFilter> = withContext(Dispatchers.IO) {
        val filterList = ArrayList<MediaFilter>()
        for (type in daoType.getAllMediaTypes()) {
            if (!sharedPreferences.getBoolean(type.text, true)) {
                daoFilter.getFilter(type.id, FilterType.FILTERCOLUMN_TYPE)?.toMediaFilter()?.let { filterList.add(it) }
            }
        }
        filterList
    }
}
