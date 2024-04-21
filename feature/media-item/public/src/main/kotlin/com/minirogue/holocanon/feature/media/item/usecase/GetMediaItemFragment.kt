package com.minirogue.holocanon.feature.media.item.usecase

import androidx.fragment.app.Fragment

interface GetMediaItemFragment {
    operator fun invoke(itemId: Int): Fragment
    operator fun invoke(itemId: Long): Fragment
}
