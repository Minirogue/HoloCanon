package com.holocanon.library.networking.internal

import com.holocanon.library.logger.HoloLogger
import com.holocanon.library.networking.HoloResult
import com.holocanon.library.networking.HttpClientWrapper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

@Inject
@ContributesBinding(AppScope::class)
class HttpClientWrapperImpl internal constructor(
    private val httpClientProvider: Provider<HttpClient>,
    private val logger: HoloLogger,
) : HttpClientWrapper {
    override suspend fun <T> perform(httpOperation: suspend HttpClient.() -> T): HoloResult<T> {
        return httpClientProvider().use { client ->
            try {
                val result = client.httpOperation()
                HoloResult.Success(result)
            } catch (responseException: ResponseException) {
                logger.info(TAG, "http failed response", responseException)
                HoloResult.Failure(responseException)
            } catch (ioException: IOException) {
                logger.info(TAG, "http IOException", ioException)
                HoloResult.Failure(ioException)
            } catch (serializationException: SerializationException) {
                logger.info(TAG, "http serializationException", serializationException)
                HoloResult.Failure(serializationException)
            }
        }
    }

    companion object {
        private const val TAG = "HttpClientWrapperImpl"
    }
}
