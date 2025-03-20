package com.minirogue.starwarscanontracker.view

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.holocanon.library.view.ext.R
import filters.model.MediaFilter

class FilterChip(mediaFilter: MediaFilter, ctx: Context) : Chip(ctx) {

    init {
        this.text = mediaFilter.name
        this.chipIcon = ResourcesCompat.getDrawable(
            resources,
            if (mediaFilter.isPositive) R.drawable.view_ext_ic_filter_check else R.drawable.view_ext_ic_filter_x,
            null,
        )
        this.closeIcon = ResourcesCompat.getDrawable(resources, R.drawable.view_ext_ic_close, null)
        this.isChipIconVisible = mediaFilter.isActive
        this.isCloseIconVisible = mediaFilter.isActive
    }
}
