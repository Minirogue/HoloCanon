package com.holocanon.library.filters.test.bindings.fakes

import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.Boolean
import kotlin.String

internal val fakeFilters = MutableStateFlow(
    listOf(
        MediaFilter(
            id = 1,
            name = "MediaType1",
            filterType = FilterType.MediaType,
            isPositive = true,
            isActive = true
        ),
        MediaFilter(
            id = 2,
            name = "Checkbox1",
            filterType = FilterType.CheckboxOne,
            isPositive = true,
            isActive = true
        ),
        MediaFilter(
            id = 3,
            name = "Checkbox2",
            filterType = FilterType.CheckboxTwo,
            isPositive = true,
            isActive = true
        ),
        MediaFilter(
            id = 4,
            name = "Checkbox3",
            filterType = FilterType.CheckboxThree,
            isPositive = true,
            isActive = false
        ),
        MediaFilter(
            id = 5,
            name = "Series1",
            filterType = FilterType.Series,
            isPositive = true,
            isActive = false
        ),
        MediaFilter(
            id = 8,
            name = "Series2",
            filterType = FilterType.Series,
            isPositive = true,
            isActive = false
        ),
        MediaFilter(
            id = 6,
            name = "Disney",
            filterType = FilterType.Publisher,
            isPositive = true,
            isActive = false
        ),
        MediaFilter(
            id = 7,
            name = "Fox",
            filterType = FilterType.Publisher,
            isPositive = true,
            isActive = false
        ),
    )
)

internal val filterTypes = MutableStateFlow(FilterType.values().map {
    FilterGroup(it, true, it.name)
})
