package com.holocanon.library.coroutine.ext.internal.di

import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Inject
@ContributesBinding(AppScope::class)
class HolocanonDispatchersImpl : HolocanonDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
}
