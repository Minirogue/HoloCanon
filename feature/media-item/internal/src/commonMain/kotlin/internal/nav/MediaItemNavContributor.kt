package internal.nav

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.media.item.usecase.MediaItemNav
import internal.view.MediaItemScreen
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
@Inject
@ContributesBinding(AppScope::class, multibinding = true)
@SingleIn(AppScope::class)
class MediaItemNavContributor : NavContributor() {
    override fun invoke(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        setAppBar: (AppBarConfig) -> Unit,
    ) =
        with(navGraphBuilder) {
            composable<MediaItemNav> { backStackEntry ->
                LaunchedEffect(true) { setAppBar(AppBarConfig()) }
                val mediaItemNav: MediaItemNav = backStackEntry.toRoute()
                MediaItemScreen(itemId = mediaItemNav.itemId, navController = navController)
            }
        }
}
