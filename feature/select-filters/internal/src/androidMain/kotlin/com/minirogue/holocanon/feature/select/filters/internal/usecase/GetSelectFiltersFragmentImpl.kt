package com.minirogue.holocanon.feature.select.filters.internal.usecase

import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.select.filters.internal.view.FilterSelectionFragment
import com.minirogue.holocanon.feature.select.filters.usecase.GetSelectFiltersFragment
import javax.inject.Inject

class GetSelectFiltersFragmentImpl @Inject constructor() : GetSelectFiltersFragment {
    override fun invoke(): Fragment = FilterSelectionFragment()
}
