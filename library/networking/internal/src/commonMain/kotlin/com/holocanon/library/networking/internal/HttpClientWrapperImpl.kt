package com.holocanon.library.networking.internal

import android.util.Log
import com.holocanon.library.networking.HoloResult
import com.holocanon.library.networking.HttpClientWrapper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import kotlinx.serialization.SerializationException
import java.io.IOException

@Inject
@ContributesBinding(AppScope::class)
class HttpClientWrapperImpl internal constructor(
    private val httpClientProvider: Provider<HttpClient>,
) : HttpClientWrapper {
    override suspend fun <T> perform(httpOperation: suspend HttpClient.() -> T): HoloResult<T> {
        return httpClientProvider().use { client ->
            try {
                val result = client.httpOperation()
                HoloResult.Success(result)
            } catch (responseException: ResponseException) {
                Log.i(TAG, "failed response $responseException")
                HoloResult.Failure(responseException)
            } catch (ioException: IOException) {
                Log.i(TAG, "IOException: $ioException")
                HoloResult.Failure(ioException)
            } catch (serializationException: SerializationException) {
                Log.i(TAG, "serializationException: $serializationException")
                HoloResult.Failure(serializationException)
            }
        }
    }

    companion object {
        private const val TAG = "HttpClientWrapperImpl"
    }
}
