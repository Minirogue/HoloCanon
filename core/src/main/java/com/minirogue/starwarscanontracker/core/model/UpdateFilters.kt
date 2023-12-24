package com.minirogue.starwarscanontracker.core.model

import android.content.Context
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.core.R
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    operator fun invoke() = GlobalScope.launch(Dispatchers.Default) {
        launch { updateSeriesFilters() }
        launch { updateCheckboxFilters() }
        launch { updateMediaTypeFilters() }
        launch { updatePublisherFilters() }
    }

    private suspend fun updatePublisherFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObject?
        val publisherFilterText = "Publisher"

        val insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_PUBLISHER, true, publisherFilterText))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_PUBLISHER)
            filterType.text = publisherFilterText
            daoFilter.update(filterType)
        }

        val companyList = daoCompany.getAllNonLive()
        for (company in companyList) {
            tempFilter = daoFilter.getFilter(company.id, FilterType.FILTERCOLUMN_PUBLISHER)?.filterObject
            if (tempFilter == null) {
                tempFilter = FilterObject(company.id, FilterType.FILTERCOLUMN_PUBLISHER, false, company.companyName)
                daoFilter.insert(tempFilter)
            } else {
                tempFilter.displayText = company.companyName
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateSeriesFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObject?
        val seriesFilterText = "Series"

        val insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_SERIES, true, seriesFilterText))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_SERIES)
            filterType.text = seriesFilterText
            daoFilter.update(filterType)
        }

        val seriesList = daoSeries.getAllNonLive()
        for (series in seriesList) {
            tempFilter = daoFilter.getFilter(series.id, FilterType.FILTERCOLUMN_SERIES)?.filterObject
            if (tempFilter == null) {
                tempFilter = FilterObject(series.id, FilterType.FILTERCOLUMN_SERIES, false, series.title)
                daoFilter.insert(tempFilter)
            } else {
                tempFilter.displayText = series.title
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateMediaTypeFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObject?
        val mediaTypeText = "Media Type"

        val insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_TYPE, true, mediaTypeText))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_TYPE)
            filterType.text = mediaTypeText
            daoFilter.update(filterType)
        }

        for (mediaType in MediaType.entries) {
            val displayText = mediaType.getSerialname()
            tempFilter = daoFilter.getFilter(mediaType.legacyId, FilterType.FILTERCOLUMN_TYPE)?.filterObject
            if (tempFilter == null) {
                tempFilter = FilterObject(mediaType.legacyId, FilterType.FILTERCOLUMN_TYPE, false, displayText)
                daoFilter.insert(tempFilter)
            } else {
                tempFilter.displayText = displayText
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateCheckboxFilters() = withContext(Dispatchers.Default) {
        val injectedCheckboxText = checkboxText.first()
        var tempFilter: FilterObject?
        // add checkbox filters
        var insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_ONE,
                true,
                injectedCheckboxText[0]))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_ONE)
            filterType.text = injectedCheckboxText[0]
            daoFilter.update(filterType)
        }

        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_ONE)?.filterObject
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_ONE, false, injectedCheckboxText[0])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[0]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_TWO, true, injectedCheckboxText[1]))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_TWO)
            filterType.text = injectedCheckboxText[1]
            daoFilter.update(filterType)
        }
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_TWO)?.filterObject
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_TWO, false, injectedCheckboxText[1])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[1]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_THREE,
                true,
                injectedCheckboxText[2]))
        if (insertWorked < 0) {
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_THREE)
            filterType.text = injectedCheckboxText[2]
            daoFilter.update(filterType)
        }
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE)?.filterObject
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE, false, injectedCheckboxText[2])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[2]
            daoFilter.update(tempFilter)
        }
    }
}
