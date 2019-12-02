package com.minirogue.starwarscanontracker.view

import android.content.Context
import com.google.android.material.chip.Chip
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter

class FilterChip(fullFilter: FullFilter, ctx: Context) : Chip(ctx) {


    init {
        this.text = fullFilter.filterObject.displayText

        //var icon = resources.getDrawable(R.drawable.ic_filter_in)
        //icon = DrawableCompat.wrap(icon)
        //DrawableCompat.setTint(icon, if (fullFilter.is_positive) Color.GREEN else Color.RED)
        this.chipIcon =  resources.getDrawable(if (fullFilter.is_positive) R.drawable.ic_filter_check else R.drawable.ic_filter_x)
        this.closeIcon = resources.getDrawable(R.drawable.ic_close)
        this.isChipIconVisible = fullFilter.filterObject.active
        this.isCloseIconVisible = fullFilter.filterObject.active
    }



}