import kotlinx.coroutines.CoroutineScope

class ApplicationScope private constructor(scope: CoroutineScope) : CoroutineScope by scope {
    companion object {
        fun from(scope: CoroutineScope) = ApplicationScope(scope)
    }
}
