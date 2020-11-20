package com.minirogue.starwarscanontracker.model

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.application.SharedPreferenceBooleanLiveData
import javax.inject.Inject

class PrefsRepo @Inject constructor(
        sharedPreferences: SharedPreferences,
        application: Application,
) {

    private val _checkBoxVisibility = booleanArrayOf(true, true, true)
    val checkBoxVisibility = MediatorLiveData<BooleanArray>()

    init {
        checkBoxVisibility.addSource(SharedPreferenceBooleanLiveData(sharedPreferences,
                application.getString(R.string.user_filter_1_active),
                true)) { _checkBoxVisibility[0] = it; checkBoxVisibility.postValue(_checkBoxVisibility) }
        checkBoxVisibility.addSource(SharedPreferenceBooleanLiveData(sharedPreferences,
                application.getString(R.string.user_filter_2_active),
                true)) { _checkBoxVisibility[1] = it; checkBoxVisibility.postValue(_checkBoxVisibility) }
        checkBoxVisibility.addSource(SharedPreferenceBooleanLiveData(sharedPreferences,
                application.getString(R.string.user_filter_3_active),
                true)) { _checkBoxVisibility[2] = it; checkBoxVisibility.postValue(_checkBoxVisibility) }
    }
}
