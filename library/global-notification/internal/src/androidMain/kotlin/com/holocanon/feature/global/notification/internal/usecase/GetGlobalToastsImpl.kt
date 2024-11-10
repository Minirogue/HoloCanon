package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.GlobalToastRepository
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetGlobalToastsImpl @Inject constructor(
    private val repository: GlobalToastRepository
): GetGlobalToasts {
    override fun invoke(): Flow<String> {
        return repository.getToasts()
    }
}
