package com.holocanon.library.coroutine.ext.internal

import com.holocanon.library.coroutine.ext.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

internal class ApplicationScopeImpl :
    ApplicationScope,
    CoroutineScope by MainScope()
