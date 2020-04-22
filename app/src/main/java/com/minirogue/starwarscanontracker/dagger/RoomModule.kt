package com.minirogue.starwarscanontracker.dagger

import android.app.Application
import android.content.SharedPreferences
import com.minirogue.starwarscanontracker.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.model.room.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(app : Application) {

    private val theDatabase: MediaDatabase = MediaDatabase.getMediaDataBase(app)

    @Provides
    @Singleton
    fun provideDatabase() : MediaDatabase{
        return theDatabase
    }

    @Provides
    @Singleton
    fun provideRepository(daoMedia: DaoMedia, daoType: DaoType, daoFilter: DaoFilter, daoSeries: DaoSeries, sharedPreferences: SharedPreferences): SWMRepository {
        return SWMRepository(daoMedia, daoType, daoFilter, daoSeries, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideDaoMedia(database: MediaDatabase) : DaoMedia {
        return database.daoMedia
    }

    @Provides
    @Singleton
    fun provideDaoType(database: MediaDatabase) : DaoType {
        return database.daoType
    }

    @Provides
    @Singleton
    fun provideDaoFilter(database: MediaDatabase) : DaoFilter {
        return database.daoFilter
    }

    @Provides
    @Singleton
    fun provideDaoSeries(database: MediaDatabase) : DaoSeries {
        return database.daoSeries
    }

    @Provides
    @Singleton
    fun provideDaoCompany(database: MediaDatabase): DaoCompany {
        return database.daoCompany
    }

}