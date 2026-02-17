package com.holocanon.core.testing.fakes

import com.holocanon.core.data.dao.DaoFilter
import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.entity.FilterTypeDto
import com.holocanon.core.data.pojo.FullFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDaoFilter(
    private val activeFilters: Flow<List<FullFilter>>,
) : DaoFilter {
    override fun getActiveFilters(): Flow<List<FullFilter>> = activeFilters

    override suspend fun insert(filterTypeDto: FilterTypeDto): Long = 0L
    override suspend fun update(filterTypeDto: FilterTypeDto) = Unit
    override fun getCheckBoxFilterTypes(): Flow<List<FilterTypeDto>> = flowOf(emptyList())
    override fun getAllFilterTypes(): Flow<List<FilterTypeDto>> = flowOf(emptyList())
    override suspend fun getAllFilterTypesNonLive(): List<FilterTypeDto> = emptyList()
    override suspend fun getFilterType(id: Int): FilterTypeDto =
        FilterTypeDto(typeId = id, isFilterPositive = true, text = "")
    override suspend fun insert(filterObjectDto: FilterObjectDto) = Unit
    override suspend fun update(filterObjectDto: FilterObjectDto) = Unit
    override fun getAllFilters(): Flow<List<FullFilter>> = flowOf(emptyList())
    override suspend fun getFilter(filterId: Int, typeId: Int): FullFilter? = null
}
