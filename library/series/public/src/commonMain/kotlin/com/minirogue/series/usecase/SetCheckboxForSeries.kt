package com.minirogue.series.usecase

import com.minirogue.series.model.Checkbox

interface SetCheckboxForSeries {
    suspend operator fun invoke(checkbox: Checkbox, seriesId: Int, newValue: Boolean)
}
