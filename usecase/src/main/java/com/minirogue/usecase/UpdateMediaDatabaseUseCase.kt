package com.minirogue.usecase

import com.minirogue.starwarscanontracker.core.model.CSVImporter
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import android.app.Application
import javax.inject.Inject

public class UpdateMediaDatabaseUseCase @Inject constructor(
    private val application: Application,
    private val filterUpdater: FilterUpdater,
) {
    operator fun invoke(forced: Boolean = false) {
        val csvImporter = CSVImporter(application, filterUpdater)
        csvImporter.execute(forced)
    }
}
