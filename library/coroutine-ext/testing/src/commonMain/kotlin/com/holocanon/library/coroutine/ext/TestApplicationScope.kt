package com.holocanon.library.coroutine.ext

import kotlinx.coroutines.CoroutineScope

internal class TestApplicationScope(testScope: CoroutineScope) :
    ApplicationScope,
    CoroutineScope by testScope
