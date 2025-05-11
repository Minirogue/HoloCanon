import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.holocanon.library.filter.ui.R
import filters.model.MediaFilter

@Composable
fun ActiveFilterChip(modifier: Modifier = Modifier, filter: MediaFilter, onDismiss: () -> Unit) {
    InputChip(
        modifier = modifier,
        onClick = { onDismiss() },
        label = { Text(filter.name) },
        selected = filter.isPositive,
        leadingIcon = {
            Icon(
                if (filter.isPositive) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = if (filter.isPositive) {
                    stringResource(R.string.filter_ui_include)
                } else {
                    stringResource(R.string.filter_ui_exclude)
                },
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(R.string.filter_ui_dismiss_filter),
            )
        },
    )
}
