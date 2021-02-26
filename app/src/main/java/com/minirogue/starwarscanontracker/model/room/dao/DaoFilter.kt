package com.minirogue.starwarscanontracker.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoFilter {

    // FilterTypes:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(filterType: FilterType): Long

    @Update
    fun update(filterType: FilterType)

    @Query("SELECT * FROM filter_type WHERE id=3 OR id=4 OR id=5")
    fun getCheckBoxFilterTypes(): Flow<List<FilterType>>

    @Query("SELECT * FROM filter_type")
    fun getAllFilterTypes(): Flow<List<FilterType>>

    @Query("SELECT * FROM filter_type")
    fun getAllFilterTypesNonLive(): List<FilterType>

    @Query("SELECT * FROM filter_type WHERE id=:id LIMIT 1")
    fun getFilterType(id: Int): FilterType

    // FilterObjects:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(filterObject: FilterObject)

    @Update
    fun update(filterObject: FilterObject)

    @Query("SELECT * FROM filter_object")
    fun getAllFilters(): Flow<List<FilterObject>>

    @Query("SELECT * FROM filter_object WHERE filter_id=:filterId AND type_id=:typeId LIMIT 1")
    fun getFilter(filterId: Int, typeId: Int): FilterObject?

    @Query("SELECT * FROM filter_object WHERE type_id=:typeId ORDER BY filter_text")
    fun getFiltersWithType(typeId: Int): Flow<List<FilterObject>>

    // FullFilter
    @Query("SELECT filter_object.*,filter_type.is_positive FROM filter_object " +
        "INNER JOIN filter_type ON filter_object.type_id = filter_type.id WHERE filter_object.is_active = 1")
    fun getActiveFilters(): Flow<List<FullFilter>>
}
