package com.holocanon.feature.global.notification.internal.di

import com.holocanon.feature.global.notification.internal.usecase.GetGlobalToastsImpl
import com.holocanon.feature.global.notification.internal.usecase.SendGlobalToastimpl
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GlobalNotificationModule {
    @Binds
    fun bindSendGlobalToast(impl: SendGlobalToastimpl): SendGlobalToast

    @Binds
    fun bindGetGlobalToasts(impl: GetGlobalToastsImpl): GetGlobalToasts
}
