package com.minirogue.starwarscanontracker.core.result

sealed interface HoloResult<T> {
    data class Success<T>(val value: T) : HoloResult<T>
    data class Failure<T>(val reason: Throwable) : HoloResult<T>
}
