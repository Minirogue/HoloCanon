package com.holocanon.feature.global.notification.usecase

class FakeSendGlobalToast: SendGlobalToast {
    private var calledArg: String? = null

    fun calledArgument(): String? = calledArg
    override fun invoke(message: String) {
        calledArg = message
    }
}
