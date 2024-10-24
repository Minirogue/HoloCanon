package com.minirogue.starwarscanontracker.core.usecase

import kotlinx.coroutines.Job

interface UpdateFilters {
    suspend operator fun invoke(): Job
}