package com.holocanon.library.coroutine.ext.internal

import com.holocanon.library.coroutine.ext.ApplicationScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Inject
@ContributesBinding(scope = AppScope::class, binding = binding<ApplicationScope>())
internal class ApplicationScopeImpl :
    ApplicationScope,
    CoroutineScope by MainScope()
