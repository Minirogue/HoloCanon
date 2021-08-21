package com.minirogue

public class DependencyInjectorImpl : DependencyInjector {
    override fun getDatabaseAccessor(): DatabaseAccessor =
        DatabaseAccessorImpl(DriverFactory(), ResourceProviderImpl())
}
