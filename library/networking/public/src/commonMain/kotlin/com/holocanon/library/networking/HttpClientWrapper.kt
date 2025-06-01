package com.holocanon.library.networking

import io.ktor.client.HttpClient

interface HttpClientWrapper {
    /**
     * Will automatically handle closing the client as well as catching common exceptions.
     * Example httpOperation: { get("some.url/with/get/request") }.body()
     */
    suspend fun <T> perform(httpOperation: suspend HttpClient.() -> T): HoloResult<T>
}
