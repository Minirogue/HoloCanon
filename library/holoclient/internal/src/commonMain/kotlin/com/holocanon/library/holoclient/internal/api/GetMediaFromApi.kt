package com.holocanon.library.holoclient.internal.api

import com.holocanon.library.networking.HoloResult
import com.holocanon.library.networking.HttpClientWrapper
import com.minirogue.common.model.StarWarsMedia
import dev.zacsweers.metro.Inject
import io.ktor.client.call.body
import io.ktor.client.request.get

@Inject
internal class GetMediaFromApi(
    private val clientWrapper: HttpClientWrapper,
) {
    suspend operator fun invoke(): HoloResult<List<StarWarsMedia>> {
        return clientWrapper.perform {
            get("https://minirogue.github.io/holocanon-api/media.json").body()
        }
    }
}
