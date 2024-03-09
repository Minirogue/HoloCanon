package com.minirogue.starwarscanontracker.usecase

import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import com.minirogue.starwarscanontracker.view.fragment.ViewMediaItemFragment
import javax.inject.Inject

class GetMediaItemFragmentImpl @Inject constructor() : GetMediaItemFragment {
    override fun invoke(itemId: Int): Fragment = ViewMediaItemFragment.createInstance(itemId)
}

