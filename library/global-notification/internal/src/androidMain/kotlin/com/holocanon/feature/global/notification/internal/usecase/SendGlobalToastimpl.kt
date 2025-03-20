package com.holocanon.feature.global.notification.internal.usecase

import com.holocanon.feature.global.notification.internal.data.GlobalToastRepository
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import javax.inject.Inject

internal class SendGlobalToastimpl @Inject constructor(
    private val repository: GlobalToastRepository,
) : SendGlobalToast {
    override fun invoke(message: String) {
        repository.enqueueToast(message)
    }
}
