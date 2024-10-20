package com.holocanon.library.coroutine.ext

import kotlinx.coroutines.CoroutineDispatcher

interface HolocanonDispatchers {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}
