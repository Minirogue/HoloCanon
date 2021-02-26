package com.minirogue.starwarscanontracker.model

import com.minirogue.starwarscanontracker.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class FilterUpdater @Inject constructor(
    private val daoFilter: DaoFilter,
    private val daoType: DaoType,
    private val daoSeries: DaoSeries,
    private val daoCompany: DaoCompany,
    @Named("checkboxes") private val injectedCheckboxText: Array<String>,
) {

    fun updateFilters() = GlobalScope.launch(Dispatchers.Default) {
        launch { updateSeriesFilters() }
        launch { updateCheckboxFilters() }
        launch { updateMediaTypeFilters() }
        launch { updatePublisherFilters() }
    }

    fun updateJustCheckboxFilters() = GlobalScope.launch(Dispatchers.Default) { updateCheckboxFilters() }

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
            tempFilter = daoFilter.getFilter(company.id, FilterType.FILTERCOLUMN_PUBLISHER)
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
            tempFilter = daoFilter.getFilter(series.id, FilterType.FILTERCOLUMN_SERIES)
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

        val mediaTypes = daoType.allNonLive
        for (mediaType in mediaTypes) {
            tempFilter = daoFilter.getFilter(mediaType.id, FilterType.FILTERCOLUMN_TYPE)
            if (tempFilter == null) {
                tempFilter = FilterObject(mediaType.id, FilterType.FILTERCOLUMN_TYPE, false, mediaType.text)
                daoFilter.insert(tempFilter)
            } else {
                tempFilter.displayText = mediaType.text
                daoFilter.update(tempFilter)
            }
        }
    }

    private suspend fun updateCheckboxFilters() = withContext(Dispatchers.Default) {
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

        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_ONE)
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
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_TWO)
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
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE)
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE, false, injectedCheckboxText[2])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = injectedCheckboxText[2]
            daoFilter.update(tempFilter)
        }
    }
}
