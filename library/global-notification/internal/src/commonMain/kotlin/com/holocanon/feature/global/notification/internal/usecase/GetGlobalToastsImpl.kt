package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.GlobalToastRepository
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class)
class GetGlobalToastsImpl internal constructor(
    private val repository: GlobalToastRepository,
) : GetGlobalToasts {
    override fun invoke(): Flow<String> {
        return repository.getToasts()
    }
}
