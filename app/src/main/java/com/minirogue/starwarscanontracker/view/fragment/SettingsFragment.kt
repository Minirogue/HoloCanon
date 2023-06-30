package com.minirogue.starwarscanontracker.view.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.usecase.UpdateMediaDatabaseUseCase
import dagger.hilt.android.AndroidEntryPoint
import filters.GetFilter
import filters.UpdateFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment :
    PreferenceFragmentCompat() {

    companion object {
        private const val TAG = "view.SettingsFragment"
    }

    @Inject
    lateinit var updateMediaDatabaseUseCase: UpdateMediaDatabaseUseCase

    @Inject
    lateinit var filterUpdater: FilterUpdater

    @Inject
    lateinit var getFilter: GetFilter

    @Inject
    lateinit var updateFilter: UpdateFilter

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        SetTypePreferences(
            requireActivity().applicationContext,
            findPreference("permanent_filters")!!
        ).execute()
        val checkboxone =
            findPreference<EditTextPreference>(getString(R.string.checkbox1_default_text))
        checkboxone?.setSummaryProvider { checkboxone.text }
        val checkboxtwo =
            findPreference<EditTextPreference>(getString(R.string.checkbox2_default_text))
        checkboxtwo?.setSummaryProvider { checkboxtwo.text }
        val checkboxthree =
            findPreference<EditTextPreference>(getString(R.string.checkbox3_default_text))
        checkboxthree?.setSummaryProvider { checkboxthree.text }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == "update_from_online") {
            // TODO work should be moved to a viewmodel
            GlobalScope.launch { updateMediaDatabaseUseCase(true) }
        } else if (preference.parent?.key == "permanent_filters") {
            GlobalScope.launch(Dispatchers.Default) {
                val filter = getFilter(preference.order, FilterType.FILTERCOLUMN_TYPE)
                filter?.let {
                    val newMediaFilter = it.copy(isActive = false)
                    updateFilter(newMediaFilter)
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    class SetTypePreferences(
        ctx: Context,
        category: PreferenceCategory,
    ) : AsyncTask<Void, Void, Unit>() {
        private val ctxRef = WeakReference(ctx)
        private val catRef = WeakReference(category)

        override fun doInBackground(vararg p0: Void?) {
            return
        }

        override fun onPostExecute(result: Unit?) {
            val types = MediaType.values()
            val ctx = ctxRef.get()
            if (ctx != null) {
                for (type in types) {
                    val text = type.getSerialname()
                    val newPref = CheckBoxPreference(ctx)
                    newPref.setDefaultValue(true)
                    newPref.title = text
                    newPref.key = text
                    newPref.order = type.legacyId
                    catRef.get()?.addPreference(newPref)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        filterUpdater.updateJustCheckboxFilters()
    }
}
