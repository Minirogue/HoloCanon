package com.holocanon.library.serialization.ext.internal.di

import com.holocanon.library.serialization.ext.internal.HolocanonJson
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
interface SerializationDependencyGraph {
    @Provides
    fun providesJson(): Json = HolocanonJson()
}
