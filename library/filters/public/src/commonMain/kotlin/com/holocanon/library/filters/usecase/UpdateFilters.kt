package com.holocanon.library.filters.usecase

import kotlinx.coroutines.Job

interface UpdateFilters {
    suspend operator fun invoke(): Job
}
