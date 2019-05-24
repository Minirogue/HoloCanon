package com.minirogue.starwarsmediatracker

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minirogue.starwarsmediatracker.database.CSVImporter
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val fragmentLayout = inflater.inflate(R.layout.fragment_settings, container, false)
        fragmentLayout.syncOnlineButton.setOnClickListener { updateDatabaseFromOnline() }
        return fragmentLayout
    }

    fun updateDatabaseFromOnline() {
        val importer = CSVImporter(activity)
        importer.execute(CSVImporter.SOURCE_ONLINE)
    }

}
