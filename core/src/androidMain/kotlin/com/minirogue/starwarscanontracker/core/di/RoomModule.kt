package com.minirogue.starwarscanontracker.core.di

import android.app.Application
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.usecase.UpdateFilters
import com.minirogue.starwarscanontracker.core.usecase.UpdateFiltersImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RoomModule {
    @Binds
    fun bindUpdateFilters(impl: UpdateFiltersImpl): UpdateFilters

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(application: Application): MediaDatabase =
            MediaDatabase.getMediaDatabase(application)

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
