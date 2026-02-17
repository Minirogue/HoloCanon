package com.holocanon.library.filters.internal

import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.pojo.FullFilter
import com.holocanon.core.testing.fakes.FakeDaoFilter
import com.holocanon.library.filters.testing.fakes.FakeGetPermanentFilters
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetActiveFiltersImplTest {

    private lateinit var activeFlow: MutableStateFlow<List<FullFilter>>
    private lateinit var permanentFlow: MutableStateFlow<List<MediaFilter>>
    private lateinit var daoFilter: FakeDaoFilter
    private lateinit var getPermanentFilters: FakeGetPermanentFilters
    private lateinit var getActiveFilters: GetActiveFiltersImpl

    @BeforeTest
    fun setup() {
        activeFlow = MutableStateFlow(emptyList())
        permanentFlow = MutableStateFlow(emptyList())
        daoFilter = FakeDaoFilter(activeFilters = activeFlow)
        getPermanentFilters = FakeGetPermanentFilters(permanentFlow)
        getActiveFilters = GetActiveFiltersImpl(daoFilter, getPermanentFilters)
    }

    @Test
    fun `invoke returns active filters from dao converted to MediaFilter when no permanent filters`() = runTest {
        // Arrange
        activeFlow.value = listOf(
            fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
            fullFilter(id = 2, name = "Checkbox1", filterType = FilterType.CheckboxOne),
        )
        permanentFlow.value = emptyList()

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("MediaType1", result[0].name)
        assertEquals(FilterType.MediaType, result[0].filterType)
        assertEquals(2, result[1].id)
        assertEquals("Checkbox1", result[1].name)
        assertEquals(FilterType.CheckboxOne, result[1].filterType)
    }

    @Test
    fun `invoke excludes active filters that are in permanent filters`() = runTest {
        // Arrange
        activeFlow.value = listOf(
            fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
            fullFilter(id = 2, name = "Checkbox1", filterType = FilterType.CheckboxOne),
        )
        permanentFlow.value = listOf(
            MediaFilter(
                id = 1,
                name = "MediaType1",
                filterType = FilterType.MediaType,
                isPositive = true,
                isActive = true,
            ),
        )

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals(2, result[0].id)
        assertEquals("Checkbox1", result[0].name)
    }

    @Test
    fun `invoke returns empty list when dao has no active filters`() = runTest {
        // Arrange
        activeFlow.value = emptyList()
        permanentFlow.value = emptyList()

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns empty list when all active filters are permanent`() = runTest {
        // Arrange
        activeFlow.value = listOf(
            fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
        )
        permanentFlow.value = listOf(
            MediaFilter(
                id = 1,
                name = "MediaType1",
                filterType = FilterType.MediaType,
                isPositive = true,
                isActive = true,
            ),
        )

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns active filter when one active and no permanent filters`() = runTest {
        // Arrange
        activeFlow.value = listOf(fullFilter(id = 1, name = "A", filterType = FilterType.MediaType))
        permanentFlow.value = emptyList()
        val mediaFilterA = MediaFilter(1, "A", FilterType.MediaType, isPositive = true, isActive = true)

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(listOf(mediaFilterA), result)
    }

    @Test
    fun `invoke returns empty list when active filter is in permanent filters`() = runTest {
        // Arrange
        val mediaFilterA = MediaFilter(1, "A", FilterType.MediaType, isPositive = true, isActive = true)
        activeFlow.value = listOf(fullFilter(id = 1, name = "A", filterType = FilterType.MediaType))
        permanentFlow.value = listOf(mediaFilterA)

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns both active filters when two active and no permanent filters`() = runTest {
        // Arrange
        activeFlow.value = listOf(
            fullFilter(id = 1, name = "A", filterType = FilterType.MediaType),
            fullFilter(id = 2, name = "B", filterType = FilterType.Series),
        )
        permanentFlow.value = emptyList()

        // Act
        val result = getActiveFilters().first()

        // Assert
        assertEquals(2, result.size)
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
