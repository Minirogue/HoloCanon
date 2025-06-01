package com.minirogue.series.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class GetSeriesIdFromNameImpl(
    private val daoSeries: DaoSeries,
) : GetSeriesIdFromName {
    override suspend fun invoke(name: String): Int? {
        return daoSeries.getAllNonLive().firstOrNull { it.title == name }?.id
    }
}
