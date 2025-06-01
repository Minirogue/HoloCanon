package com.holocanon.library.logger.internal

import com.holocanon.library.logger.HoloLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class HoloLoggerImpl internal constructor(
    private val loggerDelegates: Set<LoggerDelegate>,
) : HoloLogger {
    override fun debug(message: String) {
        loggerDelegates.forEach { it.debug(message) }
    }

    override fun info(message: String) {
        loggerDelegates.forEach { it.info(message) }
    }

    override fun warn(message: String) {
        loggerDelegates.forEach { it.warn(message) }
    }

    override fun error(message: String) {
        loggerDelegates.forEach { it.error(message) }
    }
}
