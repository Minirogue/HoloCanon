package com.minirogue

internal interface DependencyInjector {
    fun getDatabaseAccessor(): DatabaseAccessor
}
