package com.holocanon.library.filters.internal

import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.pojo.FullFilter
import com.holocanon.core.testing.fakes.FakeDaoFilter
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetFilterImplTest {

    private lateinit var daoFilter: FakeDaoFilter
    private lateinit var getFilter: GetFilterImpl

    @BeforeTest
    fun setup() {
        daoFilter = FakeDaoFilter()
        getFilter = GetFilterImpl(daoFilter)
    }

    @Test
    fun `invoke returns MediaFilter when dao returns FullFilter`() = runTest {
        // Arrange
        daoFilter.setGetFilterResult(
            fullFilter(id = 1, name = "MediaType1", filterType = FilterType.MediaType),
        )

        // Act
        val result = getFilter(1, FilterType.MediaType.legacyIntegerConversion)

        // Assert
        assertEquals(
            MediaFilter(1, "MediaType1", FilterType.MediaType, isPositive = true, isActive = true),
            result,
        )
    }

    @Test
    fun `invoke returns null when dao returns null`() = runTest {
        // Arrange
        daoFilter.setGetFilterResult(null)

        // Act
        val result = getFilter(1, FilterType.MediaType.legacyIntegerConversion)

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke maps FullFilter with isPositive false and active false correctly`() = runTest {
        // Arrange
        daoFilter.setGetFilterResult(
            fullFilter(
                id = 2,
                name = "Checkbox1",
                filterType = FilterType.CheckboxOne,
                isPositive = false,
                active = false,
            ),
        )

        // Act
        val result = getFilter(2, FilterType.CheckboxOne.legacyIntegerConversion)

        // Assert
        assertEquals(
            MediaFilter(2, "Checkbox1", FilterType.CheckboxOne, isPositive = false, isActive = false),
            result,
        )
    }

    private fun fullFilter(
        id: Int,
        name: String,
        filterType: FilterType,
        isPositive: Boolean = true,
        active: Boolean = true,
    ): FullFilter = FullFilter(
        filterObjectDto = FilterObjectDto(
            id = id,
            filterType = filterType.legacyIntegerConversion,
            active = active,
            displayText = name,
        ),
        is_positive = isPositive,
    )
}
