package di

import com.holocanon.library.navigation.NavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet
import nav.SettingsNavContributor

@Module
@InstallIn(ActivityComponent::class)
internal interface SettingsModule {
    @Binds
    @IntoSet
    fun bindSettingsNavContributor(contributor: SettingsNavContributor): NavContributor
}
