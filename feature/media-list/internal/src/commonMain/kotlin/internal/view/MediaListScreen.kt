package internal.view

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.holocanon.library.media.item.model.MediaAndNotes
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.sorting.model.SortStyle
import com.minirogue.holocanon.feature.media.item.usecase.MediaItemNav
import compose.theme.collectAsStateSafely
import dev.zacsweers.metro.Provider
import holocanon.feature.media_list.internal.generated.resources.Res
import holocanon.feature.media_list.internal.generated.resources.media_list_search_label
import holocanon.feature.media_list.internal.generated.resources.media_list_sort
import holocanon.feature.media_list.internal.generated.resources.media_list_sort_menu_timeline
import holocanon.feature.media_list.internal.generated.resources.media_list_sort_menu_title
import holocanon.feature.media_list.internal.generated.resources.media_list_sort_release_date
import internal.viewmodel.MediaListViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import loading.LoadingScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import settings.model.CheckboxSettings

private const val CARD_FADE_DURATION = 1000
private const val CARD_PLACEMENT_DURATION = 800

@Composable
internal fun MediaListScreen(
    modifier: Modifier = Modifier,
    viewModelProvider: Provider<MediaListViewModel>,
    navController: NavController,
    setAppBar: (AppBarConfig) -> Unit,
    mediaListViewModel: MediaListViewModel = viewModel { viewModelProvider() }, // Scope to Activity
) = Column(modifier = modifier.padding(horizontal = 8.dp)) {
    val state by mediaListViewModel.state.collectAsStateSafely(null)
    val mediaList by mediaListViewModel.mediaList.collectAsStateSafely(emptyList())

    LaunchedEffect(true) {
        val config = AppBarConfig.DEFAULT
            // TODO fix this on iOS
            .copy(
                actions = listOf { AppBarAction(mediaListViewModel::setSort) },
            )
        setAppBar(config)
    }

    state?.also { nonNullState ->
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            searchTerm = nonNullState.searchTerm,
            onSearchTermChanged = mediaListViewModel::updateSearch,
        )
        FilterGroup(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 4.dp),
            activeFilters = nonNullState.activeFilters,
            sortStyle = nonNullState.sortStyle,
            onDismissFilter = mediaListViewModel::deactivateFilter,
            onTapSortStyle = mediaListViewModel::reverseSort,
        )
        MediaList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            mediaList = mediaList,
            checkboxSettings = nonNullState.checkboxSettings,
            isNetworkAllowed = nonNullState.isNetworkAllowed,
            initialScrollIndex = nonNullState.scrollPosition,
            initialScrollOffset = nonNullState.scrollOffset,
            onScroll = mediaListViewModel::onScroll,
            onBox1Clicked = mediaListViewModel::onCheckBox1Clicked,
            onBox2Clicked = mediaListViewModel::onCheckBox2Clicked,
            onBox3Clicked = mediaListViewModel::onCheckBox3Clicked,
            onItemClicked = { navController.navigate(MediaItemNav(it)) },
        )
    } ?: LoadingScreen()
}

@Composable
private fun AppBarAction(onSelectSortStyle: (Int) -> Unit) = Box {
    var isMenuOpen by remember { mutableStateOf(false) }
    IconButton(onClick = { isMenuOpen = !isMenuOpen }) {
        Icon(
            imageVector = vectorResource(Res.drawable.media_list_sort),
            contentDescription = "Sort canon list",
        )
    }
    DropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.media_list_sort_release_date)) },
            onClick = { onSelectSortStyle(SortStyle.SORT_DATE) },
        )
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.media_list_sort_menu_timeline)) },
            onClick = { onSelectSortStyle(SortStyle.SORT_TIMELINE) },
        )
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.media_list_sort_menu_title)) },
            onClick = { onSelectSortStyle(SortStyle.SORT_TITLE) },
        )
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    searchTerm: String?,
    onSearchTermChanged: (String) -> Unit,
) {
    TextField(
        modifier = modifier,
        value = searchTerm ?: "",
        onValueChange = { onSearchTermChanged(it) },
        label = { Text(stringResource(Res.string.media_list_search_label)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    )
}

@Composable
private fun MediaList(
    modifier: Modifier = Modifier,
    mediaList: List<MediaAndNotes>,
    checkboxSettings: CheckboxSettings?,
    isNetworkAllowed: Boolean,
    initialScrollIndex: Int,
    initialScrollOffset: Int,
    onScroll: (index: Int, offset: Int) -> Unit,
    onBox1Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onBox2Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onBox3Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onItemClicked: (itemId: Long) -> Unit,
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialScrollIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset,
    )
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .filter { !it } // Update saved scroll position when not scrolling
            .onEach {
                onScroll(
                    lazyListState.firstVisibleItemIndex,
                    lazyListState.firstVisibleItemScrollOffset,
                )
            }
            .launchIn(this)
    }
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(items = mediaList, key = { it.mediaItem.id }) { mediaAndNotes ->
            MediaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(
                        fadeInSpec = tween(CARD_FADE_DURATION),
                        placementSpec = tween(CARD_PLACEMENT_DURATION),
                        fadeOutSpec = tween(CARD_FADE_DURATION),
                    ),
                mediaAndNotes = mediaAndNotes,
                checkboxSettings = checkboxSettings,
                isNetworkAllowed = isNetworkAllowed,
                onBox1Clicked = onBox1Clicked,
                onBox2Clicked = onBox2Clicked,
                onBox3Clicked = onBox3Clicked,
                onItemClicked = onItemClicked,
            )
        }
    }
}
