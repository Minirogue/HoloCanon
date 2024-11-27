package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.common.model.MediaType
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObjectDto
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

internal class UpdateFiltersImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val daoSeries: DaoSeries,
    private val daoCompany: DaoCompany,
    getCheckboxSettings: GetCheckboxSettings,
) : UpdateFilters {
    private val checkboxText = getCheckboxSettings().map { checkboxSettings ->
        listOf(
            checkboxSettings.checkbox1Setting.name,
            checkboxSettings.checkbox2Setting.name,
            checkboxSettings.checkbox3Setting.name,
        )
    }

    override suspend fun invoke() = withContext(Dispatchers.Default) {
        launch { updateSeriesFilters() }
        launch { updateCheckboxFilters() }
        launch { updateMediaTypeFilters() }
        launch { updatePublisherFilters() }
    }

    private suspend fun updatePublisherFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val publisherFilterText = "Publisher" // TODO use resource

        val insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_PUBLISHER,
                true,
                publisherFilterText
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_PUBLISHER)
            filterTypeDto.text = publisherFilterText
            daoFilter.update(filterTypeDto)
        }

        val companyList = daoCompany.getAllNonLive()
        for (company in companyList) {
            tempFilter = daoFilter.getFilter(
                company.id,
                FilterTypeDto.FILTERCOLUMN_PUBLISHER
            )?.filterObjectDto
            if (tempFilter == null) {
                tempFilter = FilterObjectDto(
                    company.id,
                    FilterTypeDto.FILTERCOLUMN_PUBLISHER,
                    false,
                    company.companyName
                )
                daoFilter.insert(tempFilter)
            } else {
                tempFilter = tempFilter.copy(displayText = company.companyName)
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateSeriesFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val seriesFilterText = "Series" // TODO use resource

        val insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_SERIES,
                true,
                seriesFilterText
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_SERIES)
            filterTypeDto.text = seriesFilterText
            daoFilter.update(filterTypeDto)
        }

        val seriesList = daoSeries.getAllNonLive()
        for (series in seriesList) {
            tempFilter =
                daoFilter.getFilter(series.id, FilterTypeDto.FILTERCOLUMN_SERIES)?.filterObjectDto
            if (tempFilter == null) {
                tempFilter = FilterObjectDto(
                    series.id,
                    FilterTypeDto.FILTERCOLUMN_SERIES,
                    false,
                    series.title
                )
                daoFilter.insert(tempFilter)
            } else {
                tempFilter = tempFilter.copy(displayText = series.title)
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateMediaTypeFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val mediaTypeText = "Media Type" // TODO use resource

        val insertWorked =
            daoFilter.insert(FilterTypeDto(FilterTypeDto.FILTERCOLUMN_TYPE, true, mediaTypeText))
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_TYPE)
            filterTypeDto.text = mediaTypeText
            daoFilter.update(filterTypeDto)
        }

        for (mediaType in MediaType.entries) {
            val displayText = mediaType.getSerialName()
            tempFilter = daoFilter.getFilter(
                mediaType.legacyId,
                FilterTypeDto.FILTERCOLUMN_TYPE
            )?.filterObjectDto
            if (tempFilter == null) {
                tempFilter = FilterObjectDto(
                    mediaType.legacyId,
                    FilterTypeDto.FILTERCOLUMN_TYPE,
                    false,
                    displayText
                )
                daoFilter.insert(tempFilter)
            } else {
                tempFilter = tempFilter.copy(displayText = displayText)
                daoFilter.update(tempFilter)
            }
        }
    }

    @Suppress("LongMethod")
    private suspend fun updateCheckboxFilters() = withContext(Dispatchers.Default) {
        val checkboxGroupName = "User-Defined"
        val injectedCheckboxText = checkboxText.first()
        var tempFilter: FilterObjectDto?
        // add checkbox filters
        var insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_CHECKBOX,
                true,
                checkboxGroupName
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_CHECKBOX)
            filterTypeDto.text = checkboxGroupName
            daoFilter.update(filterTypeDto)
        }

        for (checkboxNumber in listOf(1, 2, 3)) {
            tempFilter =
                daoFilter.getFilter(
                    checkboxNumber,
                    FilterTypeDto.FILTERCOLUMN_CHECKBOX
                )?.filterObjectDto
            if (tempFilter == null) {
                tempFilter = FilterObjectDto(
                    checkboxNumber,
                    FilterTypeDto.FILTERCOLUMN_CHECKBOX,
                    false,
                    injectedCheckboxText[checkboxNumber - 1]
                )
                daoFilter.insert(tempFilter)
            } else {
                tempFilter = tempFilter.copy(displayText = injectedCheckboxText[checkboxNumber - 1])
                daoFilter.update(tempFilter)
            }
        }
    }
}
