package com.minirogue.series.usecase

import com.minirogue.series.model.Series
import kotlinx.coroutines.flow.Flow

interface GetSeries {
    operator fun invoke(seriesId: Int): Flow<Series>
}