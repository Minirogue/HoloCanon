package com.minirogue.usecase

import com.minirogue.starwarscanontracker.core.model.CSVImporter
import javax.inject.Inject

public class UpdateMediaDatabaseUseCase @Inject constructor(private val csvImporter: CSVImporter) {
    operator fun invoke(forced: Boolean = false) {
        csvImporter.execute(forced)
    }
}
