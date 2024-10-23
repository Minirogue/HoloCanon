package com.minirogue.series.usecase

import com.minirogue.series.model.Series
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.SeriesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetSeriesImpl @Inject constructor(private val daoSeries: DaoSeries) : GetSeries {
    override fun invoke(seriesId: Int): Flow<Series> = daoSeries.getSeriesFlow(seriesId)
        .map { it.toSeries() }

    private fun SeriesDto.toSeries(): Series = Series(
        name = title,
        imageUrl = imageURL
    )
}
