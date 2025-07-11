package com.holocanon.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.holocanon.core.data.entity.FilterObjectDto
import com.holocanon.core.data.entity.FilterTypeDto
import com.holocanon.core.data.pojo.FullFilter
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoFilter {

    // FilterTypes:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(filterTypeDto: FilterTypeDto): Long

    @Update
    suspend fun update(filterTypeDto: FilterTypeDto)

    @Query("SELECT * FROM filter_type WHERE id=3 OR id=4 OR id=5")
    fun getCheckBoxFilterTypes(): Flow<List<FilterTypeDto>>

    @Query("SELECT * FROM filter_type")
    fun getAllFilterTypes(): Flow<List<FilterTypeDto>>

    @Query("SELECT * FROM filter_type")
    suspend fun getAllFilterTypesNonLive(): List<FilterTypeDto>

    @Query("SELECT * FROM filter_type WHERE id=:id LIMIT 1")
    suspend fun getFilterType(id: Int): FilterTypeDto

    // FilterObjects:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(filterObjectDto: FilterObjectDto)

    @Update
    suspend fun update(filterObjectDto: FilterObjectDto)

    // FullFilter
    @Query(
        "SELECT filter_object.*,filter_type.is_positive FROM filter_object " +
            "INNER JOIN filter_type ON filter_object.type_id = filter_type.id ",
    )
    fun getAllFilters(): Flow<List<FullFilter>>

    @Query(
        "SELECT filter_object.*,filter_type.is_positive FROM filter_object " +
            "INNER JOIN filter_type ON filter_object.type_id = filter_type.id " +
            "WHERE filter_id=:filterId AND type_id=:typeId LIMIT 1",
    )
    suspend fun getFilter(filterId: Int, typeId: Int): FullFilter?

    @Query(
        "SELECT filter_object.*,filter_type.is_positive FROM filter_object " +
            "INNER JOIN filter_type ON filter_object.type_id = filter_type.id " +
            "WHERE filter_object.is_active = 1",
    )
    fun getActiveFilters(): Flow<List<FullFilter>>
}
