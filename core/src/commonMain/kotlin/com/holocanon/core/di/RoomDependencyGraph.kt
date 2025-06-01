package com.holocanon.core.di

import com.holocanon.core.data.dao.DaoCompany
import com.holocanon.core.data.dao.DaoFilter
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.data.dao.DaoSeries
import com.holocanon.core.data.database.MediaDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface RoomDependencyGraph {

    @Provides
    fun provideDaoMedia(database: MediaDatabase): DaoMedia {
        return database.getDaoMedia()
    }

    @Provides
    fun provideDaoFilter(database: MediaDatabase): DaoFilter {
        return database.getDaoFilter()
    }

    @Provides
    fun provideDaoSeries(database: MediaDatabase): DaoSeries {
        return database.getDaoSeries()
    }

    @Provides
    fun provideDaoCompany(database: MediaDatabase): DaoCompany {
        return database.getDaoCompany()
    }
}
