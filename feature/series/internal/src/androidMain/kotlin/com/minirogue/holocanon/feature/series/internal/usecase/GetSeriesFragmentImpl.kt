package com.minirogue.holocanon.feature.series.internal.usecase

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.series.GetSeriesFragment
import com.minirogue.holocanon.feature.series.internal.view.SeriesFragment
import javax.inject.Inject

internal class GetSeriesFragmentImpl @Inject constructor() : GetSeriesFragment {
    override fun invoke(seriesName: String): Fragment {
        return SeriesFragment.getFragment(seriesName = seriesName)
    }
}
