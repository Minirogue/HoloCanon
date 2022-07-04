package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.Series
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeries @Inject constructor(private val daoSeries: DaoSeries) {
    operator fun invoke(seriesId: Int): Flow<Series> = daoSeries.getSeriesFlow(seriesId)
}
