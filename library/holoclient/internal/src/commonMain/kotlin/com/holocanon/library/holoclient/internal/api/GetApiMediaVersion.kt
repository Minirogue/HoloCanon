package com.holocanon.library.holoclient.internal.api

import com.holocanon.library.networking.HoloResult
import com.holocanon.library.networking.HttpClientWrapper
import dev.zacsweers.metro.Inject
import io.ktor.client.call.body
import io.ktor.client.request.get

@Inject
internal class GetApiMediaVersion(private val clientWrapper: HttpClientWrapper) {
    internal suspend operator fun invoke(): HoloResult<Int> {
        return clientWrapper.perform {
            get("https://minirogue.github.io/holocanon-api/version.json").body()
        }
    }
}
