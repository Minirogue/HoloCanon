package com.minirogue.holoclient

import com.minirogue.starwarscanontracker.core.result.HoloResult

interface GetApiMediaVersion {
    suspend operator fun invoke(): HoloResult<Int>
}
