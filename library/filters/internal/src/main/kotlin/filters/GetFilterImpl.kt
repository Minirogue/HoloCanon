package filters

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import javax.inject.Inject

class GetFilterImpl @Inject constructor(private val daoFilter: DaoFilter): GetFilter {
   override suspend operator fun invoke(id: Int, typeId: Int): MediaFilter? = daoFilter.getFilter(id, typeId)?.toMediaFilter()
}
