package com.minirogue

internal interface ResourceProvider {
    enum class CsvResources(val filename: String) {
        PEOPLE("people.csv"),
        COMPANIES("companies.csv"),
        SERIES("series.csv"),
        MEDIA_TYPE("media_types.csv"),
        MEDIA("media.csv"),
    }

    suspend fun getCsvResource(resource: CsvResources): Collection<Map<String, String>>
}
