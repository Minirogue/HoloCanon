package com.minirogue.starwarscanontracker.core.di

import android.app.Application
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
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
