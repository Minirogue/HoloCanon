package com.minirogue.holoclient

import com.minirogue.api.media.StarWarsMedia
import com.minirogue.starwarscanontracker.core.result.HoloResult

interface GetMediaFromApi {
    suspend operator fun invoke(): HoloResult<List<StarWarsMedia>>
}