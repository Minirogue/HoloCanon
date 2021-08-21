package com.minirogue

import CompanyDto
import MediaDto
import MediaTypeDto
import PersonDto
import SeriesDto
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

public interface DatabaseAccessor {
    public suspend fun initializeFromResources(coroutineContext: CoroutineContext)
    public suspend fun getAllPeople(): List<PersonDto>
    public suspend fun getPerson(id: Long): PersonDto?
    public suspend fun getAllCompanies(): List<CompanyDto>
    public suspend fun getCompany(id: Long): CompanyDto?
    public suspend fun getAllSeries(): List<SeriesDto>
    public suspend fun getSeries(id: Long): SeriesDto?
    public suspend fun getAllMediaTypes(): List<MediaTypeDto>
    public suspend fun getMediaType(id: Long): MediaTypeDto?
    public suspend fun getAllMedia(): List<MediaDto>
    public suspend fun getMedia(id: Long): MediaDto?
}

internal class DatabaseAccessorImpl(driverFactory: DriverFactory, private val resourceProvider: ResourceProvider) :
    DatabaseAccessor {
    private val database: HolocanonDb = createDatabase(driverFactory)

    // TODO does this function actually belong here? Also, should it suspend?
    override suspend fun initializeFromResources(coroutineContext: CoroutineContext): Unit =
        withContext(coroutineContext) {
            val csvPeople = resourceProvider.getCsvResource(ResourceProvider.CsvResources.PEOPLE)
            for (entry in csvPeople) {
                launch { database.personQueries.insertPerson(entry.getValue("id").toLong(), entry["name"] ?: "") }
            }

            val csvCompanies = resourceProvider.getCsvResource(ResourceProvider.CsvResources.COMPANIES)
            for (entry in csvCompanies) {
                launch { database.companyQueries.insertCompany(entry.getValue("id").toLong(), entry["name"] ?: "") }
            }

            val csvSeries = resourceProvider.getCsvResource(ResourceProvider.CsvResources.SERIES)
            for (entry in csvSeries) {
                launch { database.seriesQueries.insertSeries(entry.getValue("id").toLong(), entry["name"] ?: "") }
            }

            val csvMediaTypes = resourceProvider.getCsvResource(ResourceProvider.CsvResources.MEDIA_TYPE)
            for (entry in csvMediaTypes) {
                launch {
                    database.mediaTypesQueries.insertType(entry.getValue("id").toLong(),
                        entry["media_type"] ?: "")
                }
            }

            val csvMedia = resourceProvider.getCsvResource(ResourceProvider.CsvResources.MEDIA)
            for (entry in csvMedia) {
                launch {
                    database.mediaQueries.insertMedia(entry.getValue("id").toLong(),
                        entry.getValue("title"),
                        entry.getValue("type"),
                        entry.getValue("image").ifBlank { null },
                        entry.getValue("released"),
                        entry.getValue("timeline").toDoubleOrNull(),
                        entry.getValue("description").ifBlank { null },
                        entry.getValue("series").ifBlank { null },
                        entry.getValue("number").toLongOrNull(),
                        entry.getValue("publisher"),
                        entry.getValue("author").toLongOrNull(),
                        entry.getValue("director").toLongOrNull(),
                        entry.getValue("illustrator").toLongOrNull())
                }
            }
        }

    override suspend fun getAllPeople(): List<PersonDto> =
        database.personQueries.getAllPeople()
            .asFlow()
            .mapToList()
            .firstOrNull()
            ?.map { it.toAuthorDto() } ?: emptyList()

    override suspend fun getPerson(id: Long): PersonDto? = database.personQueries.getPerson(id)
        .asFlow()
        .mapToOneOrNull()
        .firstOrNull()
        ?.toAuthorDto()

    override suspend fun getAllCompanies(): List<CompanyDto> = database.companyQueries.getAllCompanies()
        .asFlow()
        .mapToList()
        .firstOrNull()
        ?.map { it.toCompanyDto() } ?: emptyList()

    override suspend fun getCompany(id: Long): CompanyDto? = database.companyQueries.getCompany(id)
        .asFlow()
        .mapToOneOrNull()
        .firstOrNull()
        ?.toCompanyDto()

    override suspend fun getAllSeries(): List<SeriesDto> = database.seriesQueries.getAllSeries()
        .asFlow()
        .mapToList()
        .firstOrNull()
        ?.map { it.toSeriesDto() } ?: emptyList()

    override suspend fun getSeries(id: Long): SeriesDto? = database.seriesQueries.getSeries(id)
        .asFlow()
        .mapToOneOrNull()
        .firstOrNull()
        ?.toSeriesDto()

    override suspend fun getAllMediaTypes(): List<MediaTypeDto> = database.mediaTypesQueries.getAllTypes()
        .asFlow()
        .mapToList()
        .firstOrNull()
        ?.map { it.toMediaTypeDto() } ?: emptyList()

    override suspend fun getMediaType(id: Long): MediaTypeDto? = database.mediaTypesQueries.getType(id)
        .asFlow()
        .mapToOneOrNull()
        .firstOrNull()
        ?.toMediaTypeDto()

    override suspend fun getAllMedia(): List<MediaDto> = database.mediaQueries.getAllMedia()
        .asFlow()
        .mapToList()
        .firstOrNull()
        ?.map { it.toMediaDto() } ?: emptyList()

    override suspend fun getMedia(id: Long): MediaDto? = database.mediaQueries.getMedia(id)
        .asFlow()
        .mapToOneOrNull()
        .firstOrNull()
        ?.toMediaDto()
}
