package com.minirogue.usecase

import android.app.Application
import com.minirogue.starwarscanontracker.core.model.CSVImporter
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import javax.inject.Inject

public class UpdateMediaDatabaseUseCase @Inject constructor(
    private val application: Application,
    private val filterUpdater: FilterUpdater,
) {
    operator fun invoke(forced: Boolean = false) {
//        check wifiOnly vs connMgr.isActiveNetworkMetered()
// get latest online version id
//        if (!forced && newVersionId == PreferenceManager.getDefaultSharedPreferences(appRef.get())
//                .getLong(appRef.get().getString(R.string.current_database_version), 0)
//        ) {
//            cancel(true)
//        } else if (forced) {
//            publishProgress("Updating Database")
//        }
        // import media type
        // import series
        // import company
        // import media
//        val prefs = PreferenceManager.getDefaultSharedPreferences(app)
//        val editor = prefs.edit()
//        editor.putLong(app.getString(R.string.current_database_version), newVersionId)
//        editor.apply()
//         filterUpdater.updateFilters();
        val csvImporter = CSVImporter(application, filterUpdater)
        csvImporter.execute(forced)
    }
}
