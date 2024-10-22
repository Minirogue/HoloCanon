package com.minirogue.holocanon.feature.media.item.internal.usecase

import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.media.item.internal.fragment.ViewMediaItemFragment
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import javax.inject.Inject

internal class GetMediaItemFragmentImpl @Inject constructor() : GetMediaItemFragment {
    override fun invoke(itemId: Int): Fragment = ViewMediaItemFragment.createInstance(itemId)
    override fun invoke(itemId: Long): Fragment = invoke(itemId.toInt())
}

