package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.GlobalToastRepository
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class SendGlobalToastimpl internal constructor(
    private val repository: GlobalToastRepository,
) : SendGlobalToast {
    override fun invoke(message: String) {
        repository.enqueueToast(message)
    }
}
