package com.holocanon.library.sorting.model

import com.holocanon.library.media.item.model.MediaAndNotes
import com.minirogue.common.model.StarWarsMedia
import kotlin.math.sign

class SortStyle(val style: Int, val ascending: Boolean) : Comparator<MediaAndNotes> {
    override fun compare(
        a: MediaAndNotes,
        b: MediaAndNotes,
    ): Int {
        val compNum: Int = when (style) {
            SORT_TITLE -> compareTitles(a, b)
            SORT_TIMELINE -> compareTimelines(a.mediaItem, b.mediaItem)
            SORT_DATE -> compareDates(a.mediaItem, b.mediaItem)
            else -> compareTitles(a, b)
        }
        if (ascending) {
            compNum
        } else {
            -compNum
        }
        return compNum
    }

    private fun compareTitles(p0: MediaAndNotes, p1: MediaAndNotes): Int {
        val title1 = p0.mediaItem.title
        val title2 = p1.mediaItem.title

        return title1.compareTo(title2)
    }

    private fun compareDates(item1: StarWarsMedia, item2: StarWarsMedia): Int {
        val splitDate1 = item1.releaseDate.split("/")
        val splitDate2 = item2.releaseDate.split("/")
        var compare = (splitDate1[2].toInt() - splitDate2[2].toInt())
        if (compare == 0) {
            compare = (splitDate1[0].toInt() - splitDate2[0].toInt())
        }
        if (compare == 0) {
            compare = (splitDate1[1].toInt() - splitDate2[1].toInt())
        }
        return compare
    }

    private fun compareTimelines(item1: StarWarsMedia, item2: StarWarsMedia): Int {
        val time1 = item1.timeline ?: MAX_TIMELINE_VALUE
        val time2 = item2.timeline ?: MAX_TIMELINE_VALUE
        return sign(time1 - time2).toInt()
    }

    fun getText(): String {
        return getSortText(style)
    }

    companion object {
        private const val MAX_TIMELINE_VALUE = 9999f
        const val SORT_TITLE = 1
        const val SORT_TIMELINE = 2
        const val SORT_DATE = 4
        val DEFAULT_STYLE = SortStyle(SORT_DATE, true)
        fun getSortText(sortType: Int): String {
            return when (sortType) {
                SORT_TITLE -> "Title"
                SORT_TIMELINE -> "Timeline"
                SORT_DATE -> "Date Released"
                else -> "SortTypeNotFound"
            }
        }
    }
}
