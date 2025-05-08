package com.minirogue.holocanon.feature.media.item.internal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.holocanon.feature.media.item.internal.R
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewMediaItemFragment : Fragment() {

    private val viewModel: ViewMediaItemViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(ITEM_ID_KEY, -1) ?: -1
        if (bundleItemId != -1) viewModel.setItemId(bundleItemId)

        return ComposeView(requireContext()).apply {
            setContent {
                HolocanonTheme {
                    val state = viewModel.state.collectAsState()
                    Scaffold(Modifier.fillMaxSize()) { padding ->
                        ViewMediaItemScreen(
                            Modifier.padding(padding),
                            state.value,
                            onBox1Clicked = { viewModel.toggleCheckbox1(it) },
                            onBox2Clicked = { viewModel.toggleCheckbox2(it) },
                            onBox3Clicked = { viewModel.toggleCheckbox3(it) },
                            onSeriesClicked = {
                                navigationViewModel
                                    .navigateTo(NavigationDestination.SeriesScreen(it))
                            }
                        )
                    }
                }
            }
        }
    }

    // TODO need to adjust paddings
    @Composable
    private fun ViewMediaItemScreen(
        modifier: Modifier,
        state: ViewMediaItemState,
        onBox1Clicked: (newValue: Boolean) -> Unit,
        onBox2Clicked: (newValue: Boolean) -> Unit,
        onBox3Clicked: (newValue: Boolean) -> Unit,
        onSeriesClicked: (seriesName: String) -> Unit,
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            state.mediaItem?.title?.also {
                Text(
                    text = it,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                MediaItemViewLeftColumn(
                    modifier = Modifier.fillMaxWidth(fraction = .5f),
                    imageUrl = state.mediaItem?.imageUrl,
                    description = state.mediaItem?.description,
                    isNetworkAllowed = state.isNetworkAllowed
                )
                MediaItemRightColumn(
                    modifier = Modifier.fillMaxWidth(fraction = .5f),
                    mediaItem = state.mediaItem,
                    mediaNotes = state.mediaNotes,
                    checkboxText = state.checkboxText,
                    checkboxVisibility = state.checkboxVisibility,
                    onSeriesClicked = onSeriesClicked,
                    onBox1Clicked = onBox1Clicked,
                    onBox2Clicked = onBox2Clicked,
                    onBox3Clicked = onBox3Clicked,
                )
            }
        }


    }

    @Composable
    private fun MediaItemViewLeftColumn(
        modifier: Modifier = Modifier,
        imageUrl: String?,
        description: String?,
        isNetworkAllowed: Boolean,
    ) = Column(modifier = modifier.fillMaxHeight()) {
        imageUrl?.also {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .networkCachePolicy(if (isNetworkAllowed) CachePolicy.ENABLED else CachePolicy.DISABLED)
                    .build(),
                contentDescription = "Media Cover",// TODO improve description
                placeholder = painterResource(R.drawable.common_resources_app_icon)
            )
        }
        description?.also {
            // TODO use formatted string instead of concatenation
            if (it.isNotBlank()) Text(getString(R.string.media_item_description_header) + " " + it)
        }
    }

    @Composable
    private fun MediaItemRightColumn(
        modifier: Modifier = Modifier,
        mediaItem: StarWarsMedia?,
        mediaNotes: MediaNotes?,
        checkboxText: Array<String>,
        checkboxVisibility: BooleanArray,
        onSeriesClicked: (seriesName: String) -> Unit,
        onBox1Clicked: (newValue: Boolean) -> Unit,
        onBox2Clicked: (newValue: Boolean) -> Unit,
        onBox3Clicked: (newValue: Boolean) -> Unit,
    ) = Column(modifier = modifier.fillMaxHeight()) {
        mediaItem?.type?.getSerialName()?.also { Text(it) }
        mediaItem?.releaseDate?.also { Text(it) }
        mediaItem?.series?.takeIf { it.isNotBlank() }?.also {
            TextButton({ onSeriesClicked(it) }) { Text(getString(R.string.media_item_view_series)) }
        }
        Spacer(Modifier.weight(1f))

        CheckboxGroup(
            Modifier,
            mediaNotes,
            checkboxText,
            checkboxVisibility,
            onBox1Clicked,
            onBox2Clicked,
            onBox3Clicked
        )
    }

    @Composable
    private fun CheckboxGroup(
        modifier: Modifier = Modifier,
        mediaNotes: MediaNotes?,
        checkboxText: Array<String>,
        checkboxVisibility: BooleanArray,
        onBox1Clicked: (newValue: Boolean) -> Unit,
        onBox2Clicked: (newValue: Boolean) -> Unit,
        onBox3Clicked: (newValue: Boolean) -> Unit,
    ) = Column(modifier) {
        val isBox1Checked = mediaNotes?.isBox1Checked
        val isBox2Checked = mediaNotes?.isBox2Checked
        val isBox3Checked = mediaNotes?.isBox3Checked
        if (checkboxVisibility[0] && isBox1Checked != null) {
            TextAndCheckbox(
                checkboxText[0],
                isBox1Checked
            ) { onBox1Clicked(!isBox1Checked) }
        }
        if (checkboxVisibility[1] && isBox2Checked != null) {
            TextAndCheckbox(
                checkboxText[1],
                isBox2Checked
            ) { onBox2Clicked(!isBox2Checked) }
        }
        if (checkboxVisibility[2] && isBox3Checked != null) {
            TextAndCheckbox(
                checkboxText[2],
                isBox3Checked
            ) { onBox3Clicked(!isBox3Checked) }
        }
    }

    @Composable
    private fun TextAndCheckbox(text: String, isBoxChecked: Boolean, onClick: () -> Unit) =
        Row(Modifier.clickable { onClick() }) {
            Text(text = text)
            Checkbox(checked = isBoxChecked, onCheckedChange = null)
        }

    companion object {
        private const val ITEM_ID_KEY = "item-id"
        fun createInstance(itemId: Int): ViewMediaItemFragment {
            return ViewMediaItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ITEM_ID_KEY, itemId)
                }
            }
        }
    }
}
