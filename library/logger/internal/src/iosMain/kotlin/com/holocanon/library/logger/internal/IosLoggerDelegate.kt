package com.holocanon.library.logger.internal

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal

@Inject
@ContributesIntoSet(AppScope::class)
@OptIn(ExperimentalForeignApi::class)
class IosLoggerDelegate : LoggerDelegate {
    override fun debug(message: String, tag: String?, throwable: Throwable?) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_DEBUG,
            "%s",
            "DEBUG/$tag: $message $throwable",
        )
    }

    override fun info(message: String, tag: String?, throwable: Throwable?) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_INFO,
            "%s",
            "I/$tag: $message $throwable",
        )
    }

    override fun warn(message: String, tag: String?, throwable: Throwable?) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_ERROR,
            "%s",
            "W/$tag: $message $throwable",
        )
    }

    override fun error(message: String, tag: String?, throwable: Throwable) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_FAULT,
            "%s",
            "E/$tag: $message $throwable",
        )
    }
}
