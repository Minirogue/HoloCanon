package com.minirogue.holoclient.api

import android.util.Log
import com.minirogue.common.model.StarWarsMedia
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

private const val TAG = "GetMediaFromApiImpl"

@Inject
internal class GetMediaFromApi(
    val json: Json,
) {
    suspend operator fun invoke(): HoloResult<List<StarWarsMedia>> {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
        }.use { client ->
            try {
                val result: List<StarWarsMedia> =
                    client.get("https://minirogue.github.io/holocanon-api/media.json").body()
                HoloResult.Success(result)
            } catch (responseException: ResponseException) {
                Log.i(TAG, "failed response $responseException")
                HoloResult.Failure(responseException)
            } catch (ioException: IOException) {
                Log.i(TAG, "IOException: $ioException")
                HoloResult.Failure(ioException)
            } catch (serializationException: SerializationException) {
                Log.w(TAG, "serializationException: $serializationException")
                HoloResult.Failure(serializationException)
            }
        }
    }
}
