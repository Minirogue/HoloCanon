package com.minirogue.holoclient.usecase

interface MaybeUpdateMediaDatabase {
    operator fun invoke(forced: Boolean = false)
}