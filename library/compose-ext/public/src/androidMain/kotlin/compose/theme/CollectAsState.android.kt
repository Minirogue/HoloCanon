package compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> StateFlow<T>.collectAsStateSafely(): State<T> =
    collectAsStateWithLifecycle()

@Composable
actual fun <T> Flow<T>.collectAsStateSafely(initialState: T): State<T> =
    collectAsStateWithLifecycle(initialState)
