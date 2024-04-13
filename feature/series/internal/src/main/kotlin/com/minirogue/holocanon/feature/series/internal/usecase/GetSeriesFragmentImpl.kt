package com.minirogue.holocanon.feature.series.internal.usecase

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.series.GetSeriesFragment
import com.minirogue.holocanon.feature.series.internal.view.SeriesFragment
import javax.inject.Inject

internal class GetSeriesFragmentImpl @Inject constructor() : GetSeriesFragment {
    override fun invoke(seriesId: Int): Fragment {
        val bundle = Bundle().apply {
            putInt(SeriesFragment.SERIES_ID_BUNDLE_KEY, seriesId)
        }

        return SeriesFragment().apply { arguments = bundle }
    }
}