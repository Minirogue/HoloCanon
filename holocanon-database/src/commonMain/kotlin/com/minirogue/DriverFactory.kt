package com.minirogue

import com.squareup.sqldelight.db.SqlDriver

public expect class DriverFactory {
    // https://cashapp.github.io/sqldelight/multiplatform_sqlite/
    public fun createDriver(): SqlDriver
}

internal fun createDatabase(driverFactory: DriverFactory): HolocanonDb {
    val driver = driverFactory.createDriver()
    return HolocanonDb(driver)
}
