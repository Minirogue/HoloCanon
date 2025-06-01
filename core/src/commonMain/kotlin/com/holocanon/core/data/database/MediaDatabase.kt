package com.holocanon.core.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.holocanon.core.data.dao.DaoCompany
import com.holocanon.core.data.dao.DaoFilter
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.data.dao.DaoSeries
import com.holocanon.core.data.entity.CompanyDto
import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.entity.FilterTypeDto
import com.holocanon.core.data.entity.MediaItemDto
import com.holocanon.core.data.entity.MediaNotesDto

@Database(
    entities = [MediaItemDto::class, MediaNotesDto::class, com.holocanon.core.data.entity.SeriesDto::class, FilterObjectDto::class, FilterTypeDto::class, CompanyDto::class],
    version = 20,
    autoMigrations = [
        AutoMigration(from = 17, to = 18), AutoMigration(
            from = 18,
            to = 19,
            spec = MediaDatabase.AutoMigration18To19::class,
        ), AutoMigration(from = 19, to = 20, spec = MediaDatabase.AutoMigration19To20::class),
    ],
    exportSchema = true,
)
@ConstructedBy(MediaDatabaseConstructor::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun getDaoMedia(): DaoMedia
    abstract fun getDaoSeries(): DaoSeries
    abstract fun getDaoFilter(): DaoFilter
    abstract fun getDaoCompany(): DaoCompany

    @DeleteTable(tableName = "media_types")
    class AutoMigration19To20 : AutoMigrationSpec {
        override fun onPostMigrate(connection: SupportSQLiteDatabase) {
            super.onPostMigrate(connection)
        }
    }

    @DeleteTable(tableName = "media_character_join")
    @DeleteTable(tableName = "characters")
    class AutoMigration18To19 : AutoMigrationSpec {
        override fun onPostMigrate(connection: SupportSQLiteDatabase) {
            super.onPostMigrate(connection)
        }
    }
}
