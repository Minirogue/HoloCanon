package com.holocanon.library.coroutine.ext

import kotlinx.coroutines.CoroutineDispatcher

class TestHolocanonDispatchers(testDispatcher: CoroutineDispatcher) : HolocanonDispatchers {
    override val main: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
}
