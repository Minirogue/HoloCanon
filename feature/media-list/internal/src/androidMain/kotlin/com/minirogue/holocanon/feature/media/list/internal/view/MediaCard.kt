package com.minirogue.holocanon.feature.media.list.internal.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.holocanon.core.model.MediaAndNotes
import com.holocanon.feature.media.list.internal.R
import com.minirogue.media.notes.model.MediaNotes
import compose.theme.HoloImage
import settings.model.CheckboxSettings

@Composable
internal fun MediaCard(
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
        .padding(horizontal = 4.dp, vertical = 4.dp)
        .clickable { onItemClicked(mediaAndNotes.mediaItem.id) },
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = mediaAndNotes.mediaItem.title,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.headlineMedium,
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            HoloImage(
                modifier = Modifier
                    .padding(start = 16.dp, end = 12.dp, bottom = 16.dp)
                    .height(160.dp)
                    .width(100.dp)
                    .align(Alignment.Top),
                sourceUri = mediaAndNotes.mediaItem.imageUrl,
                contentDescription = stringResource(R.string.media_list_cover_art),
                isNetworkAllowed = isNetworkAllowed,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = 16.dp),
            ) {
                Text(
                    text = mediaAndNotes.mediaItem.releaseDate,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = mediaAndNotes.mediaItem.type.getSerialName(),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                CheckboxGroup(
                    Modifier.padding(top = 4.dp),
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
) = Column(
    modifier = modifier.align(Alignment.End),
    horizontalAlignment = Alignment.End,
) {
    if (checkboxSettings != null) {
        if (checkboxSettings.checkbox1Setting.isInUse) {
            TextAndCheckbox(
                checkboxSettings.checkbox1Setting.name,
                mediaNotes.isBox1Checked,
                modifier = Modifier.padding(bottom = 4.dp),
            ) { onBox1Clicked(!mediaNotes.isBox1Checked) }
        }
        if (checkboxSettings.checkbox2Setting.isInUse) {
            TextAndCheckbox(
                checkboxSettings.checkbox2Setting.name,
                mediaNotes.isBox2Checked,
                modifier = Modifier.padding(bottom = 4.dp),
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
private fun TextAndCheckbox(
    text: String,
    isBoxChecked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Row(
    modifier
        .clickable { onClick() }
        .padding(horizontal = 4.dp, vertical = 2.dp),
) {
    Text(text = text, modifier = Modifier.padding(end = 12.dp))
    Checkbox(checked = isBoxChecked, onCheckedChange = null)
}
