package com.minirogue.holocanon.feature.media.list.internal.view

import ActiveFilterChip
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.NavController
import com.holocanon.feature.media.list.internal.R
import com.holocanon.library.navigation.AppBarConfig
import com.minirogue.holocanon.feature.media.item.usecase.MediaItemNav
import com.minirogue.holocanon.feature.media.list.internal.viewmodel.MediaListViewModel
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.SortStyle
import compose.theme.HoloImage
import filters.model.MediaFilter
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import settings.model.CheckboxSettings

private const val CARD_FADE_DURATION = 1000
private const val CARD_PLACEMENT_DURATION = 800

@Composable
internal fun MediaListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    setAppBar: (AppBarConfig) -> Unit,
    mediaListViewModel: MediaListViewModel = LocalView.current.findViewTreeViewModelStoreOwner()
        ?.let { hiltViewModel(it) } ?: hiltViewModel(), // Scope to Activity
) = Column(modifier = modifier) {
    val state by mediaListViewModel.state.collectAsStateWithLifecycle()
    val mediaList by mediaListViewModel.mediaList.collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(true) {
        val config = AppBarConfig(
            actions = listOf({ AppBarAction(mediaListViewModel::setSort) }),
        )
        setAppBar(config)
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        searchTerm = state.searchTerm,
        onSearchTermChanged = mediaListViewModel::updateSearch,
    )
    FilterGroup(
        modifier = Modifier.fillMaxWidth(),
        activeFilters = state.activeFilters,
        sortStyle = state.sortStyle,
        onDismissFilter = mediaListViewModel::deactivateFilter,
        onTapSortStyle = mediaListViewModel::reverseSort,
    )
    MediaList(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(top = 2.dp),
        mediaList = mediaList,
        checkboxSettings = state.checkboxSettings,
        isNetworkAllowed = state.isNetworkAllowed,
        initialScrollIndex = state.scrollPosition,
        initialScrollOffset = state.scrollOffset,
        onScroll = mediaListViewModel::onScroll,
        onBox1Clicked = mediaListViewModel::onCheckBox1Clicked,
        onBox2Clicked = mediaListViewModel::onCheckBox2Clicked,
        onBox3Clicked = mediaListViewModel::onCheckBox3Clicked,
        onItemClicked = { navController.navigate(MediaItemNav(it)) },
    )
}

@Composable
private fun AppBarAction(onSelectSortStyle: (Int) -> Unit) = Box {
    var isMenuOpen by remember { mutableStateOf(false) }
    IconButton(onClick = { isMenuOpen = !isMenuOpen }) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.media_list_sort),
            contentDescription = "Sort canon list",
        )
    }
    DropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.media_list_sort_release_date)) },
            onClick = { onSelectSortStyle(SortStyle.SORT_DATE) },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.media_list_sort_menu_timeline)) },
            onClick = { onSelectSortStyle(SortStyle.SORT_TIMELINE) },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.media_list_sort_menu_title)) },
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
        label = { Text(stringResource(R.string.media_list_search_label)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    )
}

@Composable
private fun FilterGroup(
    modifier: Modifier = Modifier,
    activeFilters: List<MediaFilter>,
    sortStyle: SortStyle,
    onTapSortStyle: () -> Unit,
    onDismissFilter: (MediaFilter) -> Unit,
) = FlowRow(modifier = modifier) {
    val sortArrowRotation = animateFloatAsState(
        targetValue = if (sortStyle.ascending) 0f else -180f,
        animationSpec = tween(durationMillis = 1000),
    )
    InputChip(
        onClick = { onTapSortStyle() },
        selected = true,
        label = { Text(sortStyle.getText()) },
        leadingIcon = {
            Icon(
                modifier = Modifier.rotate(sortArrowRotation.value),
                painter = painterResource(R.drawable.media_list_ascending_sort),
                contentDescription = if (sortStyle.ascending) {
                    stringResource(R.string.media_list_ascending)
                } else {
                    stringResource(R.string.media_list_descending)
                },
            )
        },
    )
    activeFilters.forEach { ActiveFilterChip(filter = it) { onDismissFilter(it) } }
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
            MediaItem(
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

@Composable
private fun MediaItem(
    modifier: Modifier,
    mediaAndNotes: MediaAndNotes,
    checkboxSettings: CheckboxSettings?,
    isNetworkAllowed: Boolean,
    onBox1Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onBox2Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onBox3Clicked: (itemId: Long, newValue: Boolean) -> Unit,
    onItemClicked: (itemId: Long) -> Unit,
) = Card(
    modifier = modifier
        .padding(4.dp)
        .clickable { onItemClicked(mediaAndNotes.mediaItem.id) },
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = mediaAndNotes.mediaItem.title,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            HoloImage(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(160.dp)
                    .width(100.dp)
                    .align(Alignment.Top),
                sourceUri = mediaAndNotes.mediaItem.imageUrl,
                contentDescription = stringResource(R.string.media_list_cover_art),
                isNetworkAllowed = isNetworkAllowed,
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(mediaAndNotes.mediaItem.releaseDate)
                Text(mediaAndNotes.mediaItem.type.getSerialName())
                CheckboxGroup(
                    Modifier.padding(8.dp),
                    mediaAndNotes.notes,
                    checkboxSettings,
                    { onBox1Clicked(mediaAndNotes.mediaItem.id, it) },
                    { onBox2Clicked(mediaAndNotes.mediaItem.id, it) },
                    { onBox3Clicked(mediaAndNotes.mediaItem.id, it) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.CheckboxGroup(
    modifier: Modifier = Modifier,
    mediaNotes: MediaNotes,
    checkboxSettings: CheckboxSettings?,
    onBox1Clicked: (newValue: Boolean) -> Unit,
    onBox2Clicked: (newValue: Boolean) -> Unit,
    onBox3Clicked: (newValue: Boolean) -> Unit,
) = Column(modifier = modifier.align(Alignment.End), horizontalAlignment = Alignment.End) {
    if (checkboxSettings != null) {
        if (checkboxSettings.checkbox1Setting.isInUse) {
            TextAndCheckbox(
                checkboxSettings.checkbox1Setting.name,
                mediaNotes.isBox1Checked,
            ) { onBox1Clicked(!mediaNotes.isBox1Checked) }
        }
        if (checkboxSettings.checkbox2Setting.isInUse) {
            TextAndCheckbox(
                checkboxSettings.checkbox2Setting.name,
                mediaNotes.isBox2Checked,
            ) { onBox2Clicked(!mediaNotes.isBox2Checked) }
        }
        if (checkboxSettings.checkbox3Setting.isInUse) {
            TextAndCheckbox(
                checkboxSettings.checkbox3Setting.name,
                mediaNotes.isBox3Checked,
            ) { onBox3Clicked(!mediaNotes.isBox3Checked) }
        }
    }
}

@Composable
private fun TextAndCheckbox(text: String, isBoxChecked: Boolean, onClick: () -> Unit) =
    Row(
        Modifier
            .clickable { onClick() }
            .padding(4.dp),
    ) {
        Text(text = text, modifier = Modifier.padding(end = 8.dp))
        Checkbox(checked = isBoxChecked, onCheckedChange = null)
    }
