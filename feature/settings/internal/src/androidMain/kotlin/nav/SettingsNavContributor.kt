package nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.feature.settings.SettingsNav
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import view.SettingsScreen
import javax.inject.Inject

internal class SettingsNavContributor @Inject constructor() : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<SettingsNav> {
            LaunchedEffect(true) { setAppBar(AppBarConfig()) }
            SettingsScreen()
        }
    }
}
