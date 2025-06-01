package com.holocanon.library.logger.internal

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class AndroidLoggerDelegate : LoggerDelegate {
    // TODO figure out clean way to automate tagging
    override fun debug(message: String, tag: String?, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun info(message: String, tag: String?, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }

    override fun warn(message: String, tag: String?, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun error(message: String, tag: String?, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
}
