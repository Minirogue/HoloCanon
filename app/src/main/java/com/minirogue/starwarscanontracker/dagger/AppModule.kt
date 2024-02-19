package com.minirogue.starwarscanontracker.dagger

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import com.minirogue.starwarscanontracker.application.IsNetworkAllowedImpl
import com.minirogue.starwarscanontracker.core.usecase.GetMediaListWithNotes
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.UpdateNotes
import com.minirogue.starwarscanontracker.usecase.GetCheckboxTextImpl
import com.minirogue.starwarscanontracker.usecase.GetMediaItemFragmentImpl
import com.minirogue.starwarscanontracker.usecase.GetMediaListWithNotesImpl
import com.minirogue.starwarscanontracker.usecase.UpdateNotesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import settings.usecase.GetCheckboxText

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    fun bindGetMediaItemFragment(impl: GetMediaItemFragmentImpl): GetMediaItemFragment

    @Binds
    fun bindGetCheckboxText(impl: GetCheckboxTextImpl): GetCheckboxText

    @Binds
    fun bindUpdateNotes(impl: UpdateNotesImpl): UpdateNotes

    @Binds
    fun bindGetMediaListWithNotes(impl: GetMediaListWithNotesImpl): GetMediaListWithNotes

    @Binds
    fun bindIsNetworkAllowed(impl: IsNetworkAllowedImpl): IsNetworkAllowed

    companion object {
        @Provides
        fun provideContext(application: Application): Context = application

        @Provides
        fun provideConnManager(app: Application): ConnectivityManager {
            return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }
}
