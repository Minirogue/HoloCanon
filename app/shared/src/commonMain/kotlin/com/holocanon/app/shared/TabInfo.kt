package com.holocanon.app.shared

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.holocanon.feature.select.filters.FilterSelectionNav
import com.minirogue.holocanon.feature.home.screen.HomeNav
import com.minirogue.holocanon.feature.media.list.usecase.MediaListNav
import holocanon.app.shared.generated.resources.Res
import holocanon.app.shared.generated.resources.nav_filters
import holocanon.app.shared.generated.resources.nav_home
import holocanon.app.shared.generated.resources.nav_media_list
import org.jetbrains.compose.resources.StringResource

internal enum class TabInfo(val tabNameRes: StringResource, val navDestination: Any) {
    // The order here defines the order of the tabs
    HOME(Res.string.nav_home, HomeNav),
    MEDIA_LIST(Res.string.nav_media_list, MediaListNav),
    FILTERS(Res.string.nav_filters, FilterSelectionNav),
    ;

    companion object {
        fun fromNavDestination(navDestination: NavDestination): TabInfo? {
            return entries.find { navDestination.hasRoute(it.navDestination::class) }
        }
    }
}
