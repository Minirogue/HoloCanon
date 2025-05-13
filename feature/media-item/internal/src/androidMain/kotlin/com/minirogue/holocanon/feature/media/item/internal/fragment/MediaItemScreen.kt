package com.minirogue.holocanon.feature.media.item.internal.fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.holocanon.feature.media.item.internal.R
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.holocanon.feature.series.SeriesNav
import com.minirogue.media.notes.model.MediaNotes
import settings.model.CheckboxSettings

@Composable
internal fun MediaItemScreen(
    modifier: Modifier = Modifier,
    itemId: Long,
    navController: NavController,
    viewModel: ViewMediaItemViewModel = hiltViewModel(), // TODO assistedInject
) {
    LaunchedEffect(itemId) { viewModel.setItemId(itemId.toInt()) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        state.mediaItem?.title?.also {
            Text(
                modifier = Modifier.padding(8.dp),
                text = it,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            MediaItemViewLeftColumn(
                modifier = Modifier.fillMaxWidth(fraction = .5f),
                imageUrl = state.mediaItem?.imageUrl,
                description = state.mediaItem?.description,
                isNetworkAllowed = state.isNetworkAllowed,
            )
            MediaItemRightColumn(
                modifier = Modifier.fillMaxWidth(),
                mediaItem = state.mediaItem,
                mediaNotes = state.mediaNotes,
                checkboxSettings = state.checkboxSettings,
                onSeriesClicked = { navController.navigate(SeriesNav(it)) },
                onBox1Clicked = viewModel::toggleCheckbox1,
                onBox2Clicked = viewModel::toggleCheckbox2,
                onBox3Clicked = viewModel::toggleCheckbox3,
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
            modifier = Modifier.padding(4.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .networkCachePolicy(if (isNetworkAllowed) CachePolicy.ENABLED else CachePolicy.DISABLED)
                .build(),
            contentDescription = stringResource(R.string.media_item_content_description_cover_art),
            placeholder = painterResource(R.drawable.common_resources_app_icon),
            fallback = painterResource(R.drawable.common_resources_app_icon),
            error = painterResource(R.drawable.common_resources_app_icon),
        )
    }
    description?.also {
        if (it.isNotBlank()) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                text = it,
            )
        }
    }
}

@Composable
private fun MediaItemRightColumn(
    modifier: Modifier = Modifier,
    mediaItem: StarWarsMedia?,
    mediaNotes: MediaNotes?,
    checkboxSettings: CheckboxSettings?,
    onSeriesClicked: (seriesName: String) -> Unit,
    onBox1Clicked: (newValue: Boolean) -> Unit,
    onBox2Clicked: (newValue: Boolean) -> Unit,
    onBox3Clicked: (newValue: Boolean) -> Unit,
) = Column(modifier = modifier.fillMaxHeight()) {
    mediaItem?.type?.getSerialName()
        ?.also { Text(text = it, modifier = Modifier.padding(8.dp)) }
    mediaItem?.releaseDate?.also { Text(text = it, modifier = Modifier.padding(8.dp)) }
    mediaItem?.series?.takeIf { it.isNotBlank() }?.also {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onSeriesClicked(it) },
        ) { Text(stringResource(R.string.media_item_view_series)) }
    }
    Spacer(Modifier.weight(1f))

    checkboxSettings?.also {
        CheckboxGroup(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            mediaNotes,
            it,
            onBox1Clicked,
            onBox2Clicked,
            onBox3Clicked,
        )
    }
}

@Composable
private fun CheckboxGroup(
    modifier: Modifier = Modifier,
    mediaNotes: MediaNotes?,
    checkboxSettings: CheckboxSettings,
    onBox1Clicked: (newValue: Boolean) -> Unit,
    onBox2Clicked: (newValue: Boolean) -> Unit,
    onBox3Clicked: (newValue: Boolean) -> Unit,
) = Column(modifier = modifier) {
    val isBox1Checked = mediaNotes?.isBox1Checked
    val isBox2Checked = mediaNotes?.isBox2Checked
    val isBox3Checked = mediaNotes?.isBox3Checked
    if (checkboxSettings.checkbox1Setting.isInUse && isBox1Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox1Setting.name,
            isBox1Checked,
        ) { onBox1Clicked(!isBox1Checked) }
    }
    if (checkboxSettings.checkbox2Setting.isInUse && isBox2Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox2Setting.name,
            isBox2Checked,
        ) { onBox2Clicked(!isBox2Checked) }
    }
    if (checkboxSettings.checkbox3Setting.isInUse && isBox3Checked != null) {
        TextAndCheckbox(
            checkboxSettings.checkbox3Setting.name,
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
