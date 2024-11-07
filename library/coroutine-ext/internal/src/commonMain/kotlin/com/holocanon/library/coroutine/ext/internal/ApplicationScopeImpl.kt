package com.holocanon.library.coroutine.ext.internal

import com.holocanon.library.coroutine.ext.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

internal class ApplicationScopeImpl :
    ApplicationScope,
    CoroutineScope by CoroutineScope(Job() + Dispatchers.Main)
