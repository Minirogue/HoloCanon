package com.minirogue.starwarsmediatracker

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.minirogue.starwarsmediatracker.database.CSVImporter


class SettingsFragment : PreferenceFragmentCompat()/*, SharedPreferences.OnSharedPreferenceChangeListener */{



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val checkboxone = findPreference<EditTextPreference>(getString(R.string.watched_read))
        checkboxone?.setSummaryProvider { checkboxone.text }
        val checkboxtwo = findPreference<EditTextPreference>(getString(R.string.want_to_watch_read))
        checkboxtwo?.setSummaryProvider { checkboxtwo.text }
        val checkboxthree = findPreference<EditTextPreference>(getString(R.string.owned))
        checkboxthree?.setSummaryProvider { checkboxthree.text }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "update_from_online"){
            CSVImporter(this.activity).execute(CSVImporter.SOURCE_ONLINE)
        }
        return super.onPreferenceTreeClick(preference)
    }



   /*   override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
    }

   override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }*/


}
