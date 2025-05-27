package com.minirogue.holocanon.feature.series.internal.view

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.minirogue.holocanon.feature.media.item.usecase.MediaItemNav
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import settings.model.CheckboxSettings

@Composable
internal fun SeriesScreen(
    modifier: Modifier = Modifier,
    seriesName: String,
    navController: NavController,
    viewModel: SeriesViewModel = hiltViewModel(
        creationCallback = { factory: SeriesViewModel.Factory ->
            factory.create(seriesName)
        },
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            onItemClicked = { navController.navigate(MediaItemNav(it)) },
        )
        val seriesId = state.seriesId
        val checkboxSettings = state.checkboxSettings
        if (seriesId != null && checkboxSettings != null) {
            CheckboxGroup(
                seriesId = seriesId,
                mediaNotes = state.seriesNotes,
                checkboxSettings = checkboxSettings,
                onBox1Clicked = viewModel::toggleCheckbox1,
                onBox2Clicked = viewModel::toggleCheckbox2,
                onBox3Clicked = viewModel::toggleCheckbox3,
            )
        }
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
    seriesId: Int,
    mediaNotes: MediaNotes?,
    checkboxSettings: CheckboxSettings,
    onBox1Clicked: (seriesId: Int, newValue: Boolean) -> Unit,
    onBox2Clicked: (seriesId: Int, newValue: Boolean) -> Unit,
    onBox3Clicked: (seriesId: Int, newValue: Boolean) -> Unit,
) = Column(modifier = modifier) {
    val isBox1Checked = mediaNotes?.isBox1Checked
    val isBox2Checked = mediaNotes?.isBox2Checked
    val isBox3Checked = mediaNotes?.isBox3Checked
    if (checkboxSettings.checkbox1Setting.isInUse && isBox1Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox1Setting.name,
            isBox1Checked,
        ) { onBox1Clicked(seriesId, !isBox1Checked) }
    }
    if (checkboxSettings.checkbox2Setting.isInUse && isBox2Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox2Setting.name,
            isBox2Checked,
        ) { onBox2Clicked(seriesId, !isBox2Checked) }
    }
    if (checkboxSettings.checkbox3Setting.isInUse && isBox3Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox3Setting.name,
            isBox3Checked,
        ) { onBox3Clicked(seriesId, !isBox3Checked) }
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
