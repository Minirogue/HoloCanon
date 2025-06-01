package com.holocanon.library.logger.internal

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class AndroidLoggerDelegate : LoggerDelegate {
    // TODO figure out clean way to automate tagging
    override fun debug(message: String) {
        Log.d("untagged", message)
    }

    override fun info(message: String) {
        Log.i("untagged", message)
    }

    override fun warn(message: String) {
        Log.w("untagged", message)
    }

    override fun error(message: String) {
        Log.e("untagged", message)
    }
}
