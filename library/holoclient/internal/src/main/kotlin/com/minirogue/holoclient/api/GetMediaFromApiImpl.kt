package com.minirogue.holoclient.api

import android.util.Log
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.holoclient.GetMediaFromApi
import com.minirogue.starwarscanontracker.core.result.HoloResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "GetMediaFromApiImpl"
internal class GetMediaFromApiImpl @Inject constructor() : GetMediaFromApi {
    override suspend fun invoke(): HoloResult<List<StarWarsMedia>> {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
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
