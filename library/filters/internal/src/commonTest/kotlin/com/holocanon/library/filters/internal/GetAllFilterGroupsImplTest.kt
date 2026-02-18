package com.holocanon.library.filters.internal

import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.entity.FilterTypeDto
import com.holocanon.core.data.pojo.FullFilter
import com.holocanon.core.testing.fakes.FakeDaoFilter
import com.holocanon.library.filters.testing.fakes.FakeGetPermanentFilters
import com.holocanon.library.settings.usecase.FakeGetCheckboxSettings
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetAllFilterGroupsImplTest {

    private lateinit var daoFilter: FakeDaoFilter
    private lateinit var getPermanentFilters: FakeGetPermanentFilters
    private lateinit var getCheckboxSettings: FakeGetCheckboxSettings
    private lateinit var getAllFilterGroups: GetAllFilterGroupsImpl

    @BeforeTest
    fun setup() {
        daoFilter = FakeDaoFilter().apply {
            setGetAllFilterTypes(
                flowOf(
                    listOf(
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_TYPE, true, "Media Type"),
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE, false, "Checkbox 1"),
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO, true, "Checkbox 2"),
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE, false, "Checkbox 3"),
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_SERIES, false, "Series"),
                        FilterTypeDto(FilterTypeDto.FILTERCOLUMN_PUBLISHER, true, "Publisher"),
                    ),
                ),
            )
        }
        getPermanentFilters = FakeGetPermanentFilters()
        getCheckboxSettings = FakeGetCheckboxSettings()
        getAllFilterGroups = GetAllFilterGroupsImpl(daoFilter, getPermanentFilters, getCheckboxSettings)
    }

    @Test
    fun `filter groups are not represented for checkbox settings marked isInUse false`() = runTest {
        // Arrange
        getCheckboxSettings.emit(
            CheckboxSettings(
                checkbox1Setting = CheckboxSetting("C1", isInUse = false),
                checkbox2Setting = CheckboxSetting("C2", isInUse = true),
                checkbox3Setting = CheckboxSetting("C3", isInUse = true),
            ),
        )
        daoFilter.setAllFilters(flowOf(emptyList()))
        getPermanentFilters.setPermanentFilters(flowOf(emptyList()))

        // Act
        val result = getAllFilterGroups().first()

        // Assert
        assertTrue(result.keys.none { it.type == FilterType.CheckboxOne })
    }

    @Test
    fun `filters that are permanent are not included in group lists`() = runTest {
        // Arrange
        daoFilter.setAllFilters(
            flowOf(
                listOf(
                    fullFilter(id = 1, name = "Book", filterType = FilterType.MediaType),
                    fullFilter(id = 2, name = "Comic", filterType = FilterType.MediaType),
                ),
            ),
        )
        getPermanentFilters.setPermanentFilters(
            flowOf(
                listOf(
                    MediaFilter(1, "Book", FilterType.MediaType, isPositive = true, isActive = true),
                ),
            ),
        )
        getCheckboxSettings.emit(allCheckboxesInUse)

        // Act
        val result = getAllFilterGroups().first()

        // Assert
        val mediaFilters = result[FilterGroup(FilterType.MediaType, true, "Media Type")]
        assertNotNull(mediaFilters)
        assertEquals(
            listOf(
                MediaFilter(2, "Comic", FilterType.MediaType, isPositive = true, isActive = true),
            ),
            mediaFilters,
        )
    }

    @Test
    fun `filters are in the map associated with the correct filter type`() = runTest {
        // Arrange
        daoFilter.setAllFilters(
            flowOf(
                listOf(
                    fullFilter(id = 1, name = "Book", filterType = FilterType.MediaType),
                    fullFilter(id = 2, name = "Series A", filterType = FilterType.Series),
                ),
            ),
        )
        getPermanentFilters.setPermanentFilters(flowOf(emptyList()))
        getCheckboxSettings.emit(allCheckboxesInUse)

        // Act
        val result = getAllFilterGroups().first()

        // Assert
        val mediaFilters = result[FilterGroup(FilterType.MediaType, true, "Media Type")]
        val seriesFilters = result[FilterGroup(FilterType.Series, false, "Series")]
        assertNotNull(mediaFilters)
        assertNotNull(seriesFilters)
        assertEquals(
            listOf(MediaFilter(1, "Book", FilterType.MediaType, isPositive = true, isActive = true)),
            mediaFilters,
        )
        assertEquals(
            listOf(
                MediaFilter(2, "Series A", FilterType.Series, isPositive = true, isActive = true),
            ),
            seriesFilters,
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

    private val allCheckboxesInUse = CheckboxSettings(
        checkbox1Setting = CheckboxSetting("C1", isInUse = true),
        checkbox2Setting = CheckboxSetting("C2", isInUse = true),
        checkbox3Setting = CheckboxSetting("C3", isInUse = true),
    )
}
