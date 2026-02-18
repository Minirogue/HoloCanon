package com.holocanon.core.testing.fakes

import com.holocanon.core.data.dao.DaoFilter
import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.entity.FilterTypeDto
import com.holocanon.core.data.pojo.FullFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDaoFilter : DaoFilter {

    private var activeFilters: Flow<List<FullFilter>> = flowOf(emptyList())
    private var getFilterResult: FullFilter? = null
    private var allFilterTypes: Flow<List<FilterTypeDto>> = flowOf(emptyList())
    private var allFilters: Flow<List<FullFilter>> = flowOf(emptyList())

    fun setActiveFilters(flow: Flow<List<FullFilter>>) {
        activeFilters = flow
    }

    fun setGetFilterResult(filter: FullFilter?) {
        getFilterResult = filter
    }

    fun setGetAllFilterTypes(flow: Flow<List<FilterTypeDto>>) {
        allFilterTypes = flow
    }

    fun setAllFilters(flow: Flow<List<FullFilter>>) {
        allFilters = flow
    }

    override fun getActiveFilters(): Flow<List<FullFilter>> = activeFilters

    override suspend fun insert(filterTypeDto: FilterTypeDto): Long = TODO()
    override suspend fun update(filterTypeDto: FilterTypeDto) = TODO()
    override fun getCheckBoxFilterTypes(): Flow<List<FilterTypeDto>> = TODO()
    override fun getAllFilterTypes(): Flow<List<FilterTypeDto>> = allFilterTypes
    override suspend fun getAllFilterTypesNonLive(): List<FilterTypeDto> = TODO()
    override suspend fun getFilterType(id: Int): FilterTypeDto = TODO()
    override suspend fun insert(filterObjectDto: FilterObjectDto) = TODO()
    override suspend fun update(filterObjectDto: FilterObjectDto) = TODO()
    override fun getAllFilters(): Flow<List<FullFilter>> = allFilters
    override suspend fun getFilter(filterId: Int, typeId: Int): FullFilter? = getFilterResult
}
