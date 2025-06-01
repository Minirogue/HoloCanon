package com.holocanon.library.logger

interface HoloLogger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}
