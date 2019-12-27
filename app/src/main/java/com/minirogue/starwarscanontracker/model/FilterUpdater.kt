package com.minirogue.starwarscanontracker.model

import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class FilterUpdater : KoinComponent {

    private val daoFilter: DaoFilter by inject()
    private val daoType: DaoType by inject()
    private val daoSeries: DaoSeries by inject()
    private val checkboxText: Array<String> by inject(named("checkboxes"))

    fun updateFilters() = GlobalScope.launch(Dispatchers.Default) {
        launch { updateSeriesFilters() }
        launch { updateCheckboxFilters() }
        launch { updateMediaTypeFilters() }
    }

    fun updateJustCheckboxFilters() = GlobalScope.launch(Dispatchers.Default) { updateCheckboxFilters() }

    private suspend fun updateSeriesFilters() = withContext(Dispatchers.Default) {
        var tempFilter: FilterObject?
        val seriesFilterText = "Series"

        val insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_SERIES, true, seriesFilterText))
        if (insertWorked < 0){
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_SERIES)
            filterType.text = seriesFilterText
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
        if (insertWorked < 0){
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_TYPE)
            filterType.text = mediaTypeText
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
        //add checkbox filters
        var insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_ONE, true, checkboxText[0]))
        if (insertWorked < 0){
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_ONE)
            filterType.text = checkboxText[0]
        }

        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_ONE)
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_ONE, false, checkboxText[0])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = checkboxText[0]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_TWO, true, checkboxText[1]))
        if (insertWorked < 0){
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_TWO)
            filterType.text = checkboxText[1]
        }
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_TWO)
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_TWO, false, checkboxText[1])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = checkboxText[1]
            daoFilter.update(tempFilter)
        }

        insertWorked = daoFilter.insert(FilterType(FilterType.FILTERCOLUMN_CHECKBOX_THREE, true, checkboxText[2]))
        if (insertWorked < 0){
            val filterType = daoFilter.getFilterType(FilterType.FILTERCOLUMN_CHECKBOX_THREE)
            filterType.text = checkboxText[2]
        }
        tempFilter = daoFilter.getFilter(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE)
        if (tempFilter == null) {
            tempFilter = FilterObject(1, FilterType.FILTERCOLUMN_CHECKBOX_THREE, false, checkboxText[2])
            daoFilter.insert(tempFilter)
        } else {
            tempFilter.displayText = checkboxText[2]
            daoFilter.update(tempFilter)
        }
    }
}