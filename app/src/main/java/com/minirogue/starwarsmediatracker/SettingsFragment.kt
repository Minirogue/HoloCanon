package com.minirogue.starwarsmediatracker

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.minirogue.starwarsmediatracker.database.CSVImporter


class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "update_from_online"){
            CSVImporter(this.activity).execute(CSVImporter.SOURCE_ONLINE)
        }
        return super.onPreferenceTreeClick(preference)
    }

}
