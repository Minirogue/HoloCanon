package com.holocanon.feature.main.screen.internal

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.holocanon.feature.select.filters.FilterSelectionNav
import com.minirogue.holocanon.feature.media.list.usecase.MediaListNav
import holocanon.feature.main_screen.internal.generated.resources.Res
import holocanon.feature.main_screen.internal.generated.resources.nav_filters
import holocanon.feature.main_screen.internal.generated.resources.nav_home
import holocanon.feature.main_screen.internal.generated.resources.nav_media_list
import org.jetbrains.compose.resources.StringResource
import screen.HomeNav

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
