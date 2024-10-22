package com.minirogue.holocanon.feature.series

import androidx.fragment.app.Fragment

interface GetSeriesFragment {
    operator fun invoke(seriesId: Int): Fragment
}
