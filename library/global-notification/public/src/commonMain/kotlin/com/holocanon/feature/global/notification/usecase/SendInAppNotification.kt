package com.holocanon.feature.global.notification.usecase

interface SendInAppNotification {
    operator fun invoke(message: String)
}
