package com.minirogue.holocanon.feature.media.list.internal.usecase

import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.media.list.internal.view.MediaListFragment
import com.minirogue.holocanon.feature.media.list.usecase.GetMediaListFragment
import javax.inject.Inject

internal class GetMediaListFragmentImpl @Inject constructor() : GetMediaListFragment {
    override fun invoke(): Fragment {
        return MediaListFragment()
    }
}
