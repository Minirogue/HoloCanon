package com.minirogue.series.usecase

import com.holocanon.core.data.entity.SeriesDto
import com.minirogue.series.model.Series
import com.minirogue.starwarscanontracker.core.data.dao.DaoSeries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
@Inject
@ContributesBinding(AppScope::class)
class GetSeriesImpl(private val daoSeries: DaoSeries) : GetSeries {
    override fun invoke(seriesId: Int): Flow<Series> = daoSeries.getSeriesFlow(seriesId)
        .map { it.toSeries() }

    private fun com.holocanon.core.data.entity.SeriesDto.toSeries(): Series = Series(
        name = title,
        imageUrl = imageURL,
    )
}
