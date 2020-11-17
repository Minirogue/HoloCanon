package com.minirogue.starwarscanontracker.usecase

import android.app.Application
import com.minirogue.starwarscanontracker.model.CSVImporter
import javax.inject.Inject

class UpdateMediaDatabaseUseCase @Inject constructor(private val application: Application) {
    operator fun invoke(forced: Boolean = false) {
        CSVImporter(application, forced).execute(CSVImporter.SOURCE_ONLINE)
    }
}