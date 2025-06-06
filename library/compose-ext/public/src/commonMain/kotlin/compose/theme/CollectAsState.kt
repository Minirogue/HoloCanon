package compose.theme

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
expect fun <T> StateFlow<T>.collectAsStateSafely(): androidx.compose.runtime.State<T>

@Composable
expect fun <T> Flow<T>.collectAsStateSafely(initialState: T): androidx.compose.runtime.State<T>
