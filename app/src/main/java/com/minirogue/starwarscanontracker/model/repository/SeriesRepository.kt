package com.minirogue.starwarscanontracker.model.repository

import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SeriesRepository @Inject constructor(private val daoSeries: DaoSeries) {

    suspend fun getSeriesStringById(seriesId: Int): String = withContext(Dispatchers.IO) {
        daoSeries.getSeries(seriesId)?.title ?: "Series not found"
    }
}
