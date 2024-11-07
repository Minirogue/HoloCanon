package com.minirogue.series.usecase

interface GetSeriesIdFromName {
    suspend operator fun invoke(name: String): Int?
}
