package com.holocanon.library.filters.internal

import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.pojo.FullFilter
import com.holocanon.core.testing.fakes.FakeDaoFilter
import com.holocanon.library.filters.testing.fakes.FakeGetPermanentFilters
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetActiveFiltersImplTest {

    private lateinit var daoFilter: FakeDaoFilter
    private lateinit var getPermanentFilters: FakeGetPermanentFilters
    private lateinit var getActiveFilters: GetActiveFiltersImpl

    @BeforeTest
    fun setup() {
        daoFilter = FakeDaoFilter()
        getPermanentFilters = FakeGetPermanentFilters()
        getActiveFilters = GetActiveFiltersImpl(daoFilter, getPermanentFilters)
    }

    @Test
    fun `invoke returns active filters from dao converted to MediaFilter when no permanent filters`() = runTest {
        // Arrange
        daoFilter.setActiveFilters(
            flowOf(
                listOf(
                    fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
                    fullFilter(id = 2, name = "Checkbox1", filterType = FilterType.CheckboxOne),
                ),
            ),
        )
        getPermanentFilters.setPermanentFilters(flowOf(emptyList()))

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(
            listOf(
                MediaFilter(1, "MediaType1", FilterType.MediaType, isPositive = true, isActive = true),
                MediaFilter(2, "Checkbox1", FilterType.CheckboxOne, isPositive = true, isActive = true),
            ),
            result,
        )
    }

    @Test
    fun `invoke excludes active filters that are in permanent filters`() = runTest {
        // Arrange
        daoFilter.setActiveFilters(
            flowOf(
                listOf(
                    fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
                    fullFilter(id = 2, name = "Checkbox1", filterType = FilterType.CheckboxOne),
                ),
            ),
        )
        getPermanentFilters.setPermanentFilters(
            flowOf(
                listOf(
                    MediaFilter(
                        id = 1,
                        name = "MediaType1",
                        filterType = FilterType.MediaType,
                        isPositive = true,
                        isActive = true,
                    ),
                ),
            ),
        )

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(
            listOf(MediaFilter(2, "Checkbox1", FilterType.CheckboxOne, isPositive = true, isActive = true)),
            result,
        )
    }

    @Test
    fun `invoke returns empty list when dao has no active filters`() = runTest {
        // Arrange
        daoFilter.setActiveFilters(flowOf(emptyList()))
        getPermanentFilters.setPermanentFilters(flowOf(emptyList()))

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `invoke returns empty list when all active filters are permanent`() = runTest {
        // Arrange
        daoFilter.setActiveFilters(
            flowOf(listOf(fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType))),
        )
        getPermanentFilters.setPermanentFilters(
            flowOf(
                listOf(
                    MediaFilter(
                        id = 1,
                        name = "MediaType1",
                        filterType = FilterType.MediaType,
                        isPositive = true,
                        isActive = true,
                    ),
                ),
            ),
        )

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(emptyList(), result)
    }

    private fun fullFilter(
        id: Int,
        name: String,
        filterType: FilterType,
        isPositive: Boolean = true,
    ): FullFilter = FullFilter(
        filterObjectDto = FilterObjectDto(
            id = id,
            filterType = filterType.legacyIntegerConversion,
            active = true,
            displayText = name,
        ),
        is_positive = isPositive,
    )
}
