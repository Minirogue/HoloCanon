package com.minirogue.holocanon.feature.home.screen

import androidx.fragment.app.Fragment
import javax.inject.Inject

class GetHomeFragmentImpl @Inject constructor() : GetHomeFragment {
    override fun invoke(): Fragment = HomeFragment()
}
