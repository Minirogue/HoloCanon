package com.minirogue.starwarscanontracker.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.preference.PreferenceManager
import com.minirogue.starwarscanontracker.R
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val theApplication: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = theApplication

    @Provides
    @Singleton
    fun provideApplication(): Application = theApplication

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    @Singleton
    fun provideConnManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Named("checkboxes")
    fun provideCheckboxNames(sharedPreferences: SharedPreferences, app: Application): Array<String> = arrayOf(
            sharedPreferences.getString(app.getString(R.string.checkbox1_default_text), app.getString(R.string.checkbox1_default_text))!!,
            sharedPreferences.getString(app.getString(R.string.checkbox2_default_text), app.getString(R.string.checkbox2_default_text))!!,
            sharedPreferences.getString(app.getString(R.string.checkbox3_default_text), app.getString(R.string.checkbox3_default_text))!!)



}