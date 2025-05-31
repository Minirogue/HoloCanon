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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RoomModule {
    companion object {
        @Provides
        @Singleton
        fun provideDatabase(application: Application): MediaDatabase =
            MediaDatabase.createDatabase(application)

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
}
