package com.minirogue.api

import com.minirogue.common.model.StarWarsMedia
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object VerifyImageLinks {

    suspend fun invoke(mediaList: List<StarWarsMedia>) {
        val imageAddresses = mediaList.associate { it.id to it.imageUrl }
        val httpClient = HttpClient(OkHttp)

        val invalidImageLinks = imageAddresses.map { idToUrlEntry ->
            coroutineScope {
                async { idToUrlEntry.key to idToUrlEntry.value?.let { httpClient.get(it) } }
            }
        }.mapNotNull {
            if (it.await().second?.status?.value != 200) it.await().first else null
        }
        error("The following entries have missing images (either no link or dead link): $invalidImageLinks")
    }
}
