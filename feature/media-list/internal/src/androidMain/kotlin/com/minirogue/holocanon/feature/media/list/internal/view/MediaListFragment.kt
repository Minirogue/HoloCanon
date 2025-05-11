package com.minirogue.holocanon.feature.media.list.internal.view

import ActiveFilterChip
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.holocanon.feature.media.list.internal.R
import com.minirogue.holocanon.feature.media.list.internal.viewmodel.MediaListState
import com.minirogue.holocanon.feature.media.list.internal.viewmodel.MediaListViewModel
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import compose.theme.HoloImage
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import filters.model.MediaFilter
import settings.model.CheckboxSettings

@AndroidEntryPoint
internal class MediaListFragment : Fragment() {

    private val mediaListViewModel: MediaListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HolocanonTheme {
                    val state = mediaListViewModel.state.collectAsState()
                    val mediaList =
                        mediaListViewModel.mediaList.collectAsState(initial = emptyList())
                    Scaffold(Modifier.fillMaxSize()) { padding ->
                        MediaListScreen(
                            modifier = Modifier.padding(padding),
                            state = state.value,
                            mediaList = mediaList.value,
                            onBox1Clicked = { itemId, newValue ->
                                mediaListViewModel.onCheckBox1Clicked(itemId, newValue)
                            },
                            onBox2Clicked = { itemId, newValue ->
                                mediaListViewModel.onCheckBox2Clicked(itemId, newValue)
                            },
                            onBox3Clicked = { itemId, newValue ->
                                mediaListViewModel.onCheckBox3Clicked(itemId, newValue)
                            },
                            onItemClicked = {
                                navigationViewModel
                                    .navigateTo(NavigationDestination.MediaItemScreen(it))
                            },
                            onSearchTermChanged = { mediaListViewModel.updateSearch(it) },
                            onDismissFilter = { mediaListViewModel.deactivateFilter(it) },
                            onTapSortStyle = { mediaListViewModel.reverseSort() },
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuItems()
    }

    @Composable
    private fun MediaListScreen(
        modifier: Modifier = Modifier,
        state: MediaListState,
        mediaList: List<MediaAndNotes>,
        onSearchTermChanged: (String) -> Unit,
        onBox1Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onBox2Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onBox3Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onItemClicked: (itemId: Long) -> Unit,
        onDismissFilter: (MediaFilter) -> Unit,
        onTapSortStyle: () -> Unit,
    ) = Column(modifier = modifier) {
        SearchBar(modifier = Modifier.fillMaxWidth(), onSearchTermChanged = onSearchTermChanged)
        FilterGroup(
            modifier = Modifier.fillMaxWidth(),
            activeFilters = state.activeFilters,
            sortStyle = state.sortStyle,
            onDismissFilter = onDismissFilter,
            onTapSortStyle = onTapSortStyle,
        )
        MediaList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 2.dp),
            mediaList = mediaList,
            checkboxSettings = state.checkboxSettings,
            isNetworkAllowed = state.isNetworkAllowed,
            onBox1Clicked = onBox1Clicked,
            onBox2Clicked = onBox2Clicked,
            onBox3Clicked = onBox3Clicked,
            onItemClicked = onItemClicked,
        )
    }

    @Composable
    private fun SearchBar(
        modifier: Modifier = Modifier,
        onSearchTermChanged: (String) -> Unit,
    ) {
        val searchTerm: MutableState<String> = remember { mutableStateOf("") }
        TextField(
            modifier = modifier,
            value = searchTerm.value,
            onValueChange = {
                searchTerm.value = it
                onSearchTermChanged(it)
            },
            label = { Text("Search") }, // TODO extract string
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
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
                    contentDescription = null, // TODO
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
        onBox1Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onBox2Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onBox3Clicked: (itemId: Long, newValue: Boolean) -> Unit,
        onItemClicked: (itemId: Long) -> Unit,
    ) = LazyColumn(
        modifier = modifier,
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

    private fun addMenuItems() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.media_list_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.sort_by_date_menu_item -> {
                            mediaListViewModel.setSort(SortStyle.SORT_DATE)
                            true
                        }

                        R.id.sort_by_timeline_menu_item -> {
                            mediaListViewModel.setSort(SortStyle.SORT_TIMELINE)
                            true
                        }

                        R.id.sort_by_title_menu_item -> {
                            mediaListViewModel.setSort(SortStyle.SORT_TITLE)
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    companion object {
        private const val CARD_FADE_DURATION = 1000
        private const val CARD_PLACEMENT_DURATION = 800
    }
}
