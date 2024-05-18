package com.minirogue.starwarscanontracker.core.model

import android.content.Context
import com.minirogue.common.model.MediaType
import com.minirogue.starwarscanontracker.core.R
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObjectDto
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

class UpdateFilters @Inject constructor(
    private val daoFilter: DaoFilter,
    private val daoSeries: DaoSeries,
    private val daoCompany: DaoCompany,
    getCheckboxSettings: GetCheckboxSettings,
    @ApplicationContext private val context: Context,
) {
    private val checkboxText = getCheckboxSettings().map { checkboxSettings ->
        listOf(
            checkboxSettings.checkbox1Setting.name
                ?: context.getString(R.string.checkbox1_default_text),
            checkboxSettings.checkbox2Setting.name
                ?: context.getString(R.string.checkbox2_default_text),
            checkboxSettings.checkbox3Setting.name
                ?: context.getString(R.string.checkbox3_default_text),
        )
    }

    suspend operator fun invoke() = withContext(Dispatchers.Default) {
        launch { updateSeriesFilters() }
        launch { updateCheckboxFilters() }
        launch { updateMediaTypeFilters() }
        launch { updatePublisherFilters() }
    }

    private suspend fun updatePublisherFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val publisherFilterText = "Publisher"

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
                tempFilter.displayText = company.companyName
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateSeriesFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val seriesFilterText = "Series"

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
                tempFilter.displayText = series.title
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateMediaTypeFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObjectDto?
        val mediaTypeText = "Media Type"

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
                tempFilter.displayText = displayText
                daoFilter.update(tempFilter)
            }
        }
    }

    @Suppress("LongMethod")
    private suspend fun updateCheckboxFilters() = withContext(Dispatchers.Default) {
        val injectedCheckboxText = checkboxText.first()
        var tempFilter: FilterObjectDto?
        // add checkbox filters
        var insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE,
                true,
                injectedCheckboxText[0]
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE)
            filterTypeDto.text = injectedCheckboxText[0]
            daoFilter.update(filterTypeDto)
        }

        tempFilter =
            daoFilter.getFilter(1, FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE)?.filterObjectDto
        if (tempFilter == null) {
            tempFilter = FilterObjectDto(
                1,
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE,
                false,
                injectedCheckboxText[0]
            )
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[0]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO,
                true,
                injectedCheckboxText[1]
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO)
            filterTypeDto.text = injectedCheckboxText[1]
            daoFilter.update(filterTypeDto)
        }
        tempFilter =
            daoFilter.getFilter(1, FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO)?.filterObjectDto
        if (tempFilter == null) {
            tempFilter = FilterObjectDto(
                1,
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO,
                false,
                injectedCheckboxText[1]
            )
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[1]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(
            FilterTypeDto(
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE,
                true,
                injectedCheckboxText[2]
            )
        )
        if (insertWorked < 0) {
            val filterTypeDto = daoFilter.getFilterType(FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE)
            filterTypeDto.text = injectedCheckboxText[2]
            daoFilter.update(filterTypeDto)
        }
        tempFilter =
            daoFilter.getFilter(1, FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE)?.filterObjectDto
        if (tempFilter == null) {
            tempFilter = FilterObjectDto(
                1,
                FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE,
                false,
                injectedCheckboxText[2]
            )
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[2]
            daoFilter.update(tempFilter)
        }
    }
}
