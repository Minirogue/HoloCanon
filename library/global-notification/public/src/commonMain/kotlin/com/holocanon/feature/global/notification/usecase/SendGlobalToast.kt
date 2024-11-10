package com.holocanon.feature.global.notification.usecase

interface SendGlobalToast {
    operator fun invoke(message: String)
}
