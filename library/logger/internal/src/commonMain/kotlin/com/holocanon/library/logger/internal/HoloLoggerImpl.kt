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
    override fun debug(message: String, tag: String?, throwable: Throwable?) {
        loggerDelegates.forEach { it.debug(message, tag, throwable) }
    }

    override fun info(message: String, tag: String?, throwable: Throwable?) {
        loggerDelegates.forEach { it.info(message, tag, throwable) }
    }

    override fun warn(message: String, tag: String?, throwable: Throwable?) {
        loggerDelegates.forEach { it.warn(message, tag, throwable) }
    }

    override fun error(message: String, tag: String?, throwable: Throwable) {
        loggerDelegates.forEach { it.error(message, tag, throwable) }
    }
}
