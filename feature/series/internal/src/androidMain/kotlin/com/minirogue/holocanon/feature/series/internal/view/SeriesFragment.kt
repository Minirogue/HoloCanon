package com.minirogue.holocanon.feature.series.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SeriesFragment : Fragment() {

    private val viewModel: SeriesViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        arguments?.getString(SERIES_NAME_BUNDLE_KEY)?.also { viewModel.setSeries(it) }

        return ComposeView(requireContext()).apply {
            setContent {
                HolocanonTheme {
                    val state = viewModel.state.collectAsState()
                    Scaffold(Modifier.fillMaxSize()) { padding ->
                        SeriesScreen(
                            modifier = Modifier.padding(padding),
                            state = state.value,
                            onBox1Clicked = { viewModel.toggleCheckbox1(it) },
                            onBox2Clicked = { viewModel.toggleCheckbox2(it) },
                            onBox3Clicked = { viewModel.toggleCheckbox3(it) },
                            onItemClicked = { itemId ->
                                navigationViewModel.navigateTo(
                                    NavigationDestination.MediaItemScreen(
                                        itemId,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SeriesScreen(
        modifier: Modifier,
        state: SeriesState,
        onBox1Clicked: (newValue: Boolean) -> Unit,
        onBox2Clicked: (newValue: Boolean) -> Unit,
        onBox3Clicked: (newValue: Boolean) -> Unit,
        onItemClicked: (itemId: Long) -> Unit,
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = state.series.name,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.headlineMedium,
            )
// TODO add series images
//            AsyncImage(
//                modifier = Modifier.padding(4.dp),
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(state.series.imageUrl)
//                    .networkCachePolicy(if (state.isNetworkAllowed) CachePolicy.ENABLED else CachePolicy.DISABLED)
//                    .build(),
//                contentDescription = "Media Cover", // TODO improve description
//                placeholder = painterResource(R.drawable.common_resources_app_icon),
//                fallback = painterResource(R.drawable.common_resources_app_icon),
//                error = painterResource(R.drawable.common_resources_app_icon),
//            )
            SeriesList(
                modifier = Modifier.weight(1f),
                media = state.mediaAndNotes,
                onItemClicked = onItemClicked,
            )
            CheckboxGroup(
                mediaNotes = state.seriesNotes,
                checkboxText = state.checkBoxText,
                checkboxVisibility = state.checkBoxVisibility,
                onBox1Clicked = onBox1Clicked,
                onBox2Clicked = onBox2Clicked,
                onBox3Clicked = onBox3Clicked,
            )
        }
    }

    @Composable
    private fun SeriesList(
        modifier: Modifier = Modifier,
        media: List<MediaAndNotes>,
        onItemClicked: (itemId: Long) -> Unit,
    ) {
        LazyColumn(modifier = modifier) {
            items(media) {
                Text(
                    modifier = Modifier.clickable { onItemClicked(it.mediaItem.id) },
                    text = it.mediaItem.title,
                )
            }
        }
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
    ) = Column(modifier = modifier) {
        val isBox1Checked = mediaNotes?.isBox1Checked
        val isBox2Checked = mediaNotes?.isBox2Checked
        val isBox3Checked = mediaNotes?.isBox3Checked
        if (checkboxVisibility[0] && isBox1Checked != null) {
            TextAndCheckbox(
                checkboxText[0],
                isBox1Checked,
            ) { onBox1Clicked(!isBox1Checked) }
        }
        if (checkboxVisibility[1] && isBox2Checked != null) {
            TextAndCheckbox(
                checkboxText[1],
                isBox2Checked,
            ) { onBox2Clicked(!isBox2Checked) }
        }
        if (checkboxVisibility[2] && isBox3Checked != null) {
            TextAndCheckbox(
                checkboxText[2],
                isBox3Checked,
            ) { onBox3Clicked(!isBox3Checked) }
        }
    }

    @Composable
    private fun TextAndCheckbox(text: String, isBoxChecked: Boolean, onClick: () -> Unit) =
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(text = text, modifier = Modifier.padding(end = 8.dp))
            Checkbox(checked = isBoxChecked, onCheckedChange = null)
        }

    companion object {
        private const val SERIES_NAME_BUNDLE_KEY = "series-name"
        fun getFragment(seriesName: String): SeriesFragment {
            val bundle = Bundle().apply {
                putString(SERIES_NAME_BUNDLE_KEY, seriesName)
            }
            return SeriesFragment().apply { arguments = bundle }
        }
    }
}
