package screen.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import screen.HomeNav
import screen.HomeScreen

@Inject
@ContributesIntoSet(AppScope::class)
class HomeNavContributor : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) = with(navGraphBuilder) {
        composable<HomeNav> { _ ->
            LaunchedEffect(true) { setAppBar(AppBarConfig.DEFAULT) }
            HomeScreen()
        }
    }
}
