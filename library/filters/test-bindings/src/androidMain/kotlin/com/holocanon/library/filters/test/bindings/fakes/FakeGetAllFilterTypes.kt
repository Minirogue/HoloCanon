package com.holocanon.library.filters.test.bindings.fakes

import filters.GetAllFilterTypes
import filters.model.FilterGroup
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeGetAllFilterTypes @Inject constructor() : GetAllFilterTypes {
    override fun invoke(): Flow<List<FilterGroup>> = filterTypes
}