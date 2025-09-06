import com.holocanon.library.settings.usecase.IsNetworkAllowed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeIsNetworkAllowed : IsNetworkAllowed {
    private var returnFlow = MutableSharedFlow<Boolean>(replay = Int.MAX_VALUE)
    suspend fun emit(isNetworkAllowed: Boolean) {
        returnFlow.emit(isNetworkAllowed)
    }

    override fun invoke(): Flow<Boolean> {
        return returnFlow
    }
}
