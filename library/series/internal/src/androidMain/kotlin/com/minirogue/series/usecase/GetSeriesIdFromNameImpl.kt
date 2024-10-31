package com.minirogue.series.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import javax.inject.Inject

internal class GetSeriesIdFromNameImpl @Inject constructor(
    private val daoSeries: DaoSeries,
) : GetSeriesIdFromName {
    override suspend fun invoke(name: String): Int? {
        return daoSeries.getAllNonLive().firstOrNull { it.title == name }?.id
    }
}