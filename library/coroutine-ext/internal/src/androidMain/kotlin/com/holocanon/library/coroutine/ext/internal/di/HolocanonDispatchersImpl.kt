package com.holocanon.library.coroutine.ext.internal.di

import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


internal class HolocanonDispatchersImpl @Inject constructor() : HolocanonDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
}
