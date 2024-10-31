package com.minirogue.series.usecase

interface GetSeriesIdFromName {
    operator suspend fun invoke(name: String): Int?
}
