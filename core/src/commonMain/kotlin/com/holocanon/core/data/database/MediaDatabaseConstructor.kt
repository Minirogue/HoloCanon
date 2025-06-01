package com.holocanon.core.data.database

import androidx.room.RoomDatabaseConstructor

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MediaDatabaseConstructor : RoomDatabaseConstructor<MediaDatabase> {
    override fun initialize(): MediaDatabase
}
