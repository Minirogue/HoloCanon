package com.holocanon.library.logger

interface HoloLogger {
    fun debug(message: String, tag: String? = null, throwable: Throwable? = null)
    fun info(message: String, tag: String? = null, throwable: Throwable? = null)
    fun warn(message: String, tag: String? = null, throwable: Throwable? = null)
    fun error(message: String, tag: String? = null, throwable: Throwable)
}
