package compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateSafely(): State<T> = collectAsState()

@Composable
actual fun <T> Flow<T>.collectAsStateSafely(initialState: T): State<T> =
    collectAsState(initialState)
