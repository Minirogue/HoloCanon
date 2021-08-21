package com.minirogue

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

public actual class DriverFactory {
    public actual fun createDriver(): SqlDriver {
        // https://cashapp.github.io/sqldelight/multiplatform_sqlite/
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HolocanonDb.Schema.create(driver)
        return driver
    }
}
