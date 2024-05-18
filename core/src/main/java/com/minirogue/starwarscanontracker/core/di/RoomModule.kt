package com.minirogue.starwarscanontracker.core.di

import android.app.Application
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object RoomModule {

    @Provides
    fun provideDatabase(application: Application): MediaDatabase =
        MediaDatabase.getMediaDataBase(application)

    @Provides
    fun provideDaoMedia(database: MediaDatabase): DaoMedia {
        return database.daoMedia
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
