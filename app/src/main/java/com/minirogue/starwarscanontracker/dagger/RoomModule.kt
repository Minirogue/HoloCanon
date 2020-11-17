package com.minirogue.starwarscanontracker.dagger

import android.app.Application
import com.minirogue.starwarscanontracker.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.model.room.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object RoomModule {

    @Provides
    fun provideDatabase(application: Application): MediaDatabase = MediaDatabase.getMediaDataBase(application)

    @Provides
    fun provideDaoMedia(database: MediaDatabase): DaoMedia {
        return database.daoMedia
    }

    @Provides
    fun provideDaoType(database: MediaDatabase): DaoType {
        return database.daoType
    }

    @Provides
    fun provideDaoFilter(database: MediaDatabase): DaoFilter {
        return database.daoFilter
    }

    @Provides
    fun provideDaoSeries(database: MediaDatabase): DaoSeries {
        return database.daoSeries
    }

    @Provides
    fun provideDaoCompany(database: MediaDatabase): DaoCompany {
        return database.daoCompany
    }
}
