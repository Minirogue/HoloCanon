package com.minirogue.starwarscanontracker.model

import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import kotlin.math.sign


class SortStyle(val style: Int, val ascending: Boolean) : Comparator<MediaAndNotes>{

    companion object{
        const val SORT_TITLE = 1
        const val SORT_TIMELINE = 2
        const val SORT_TYPE = 3
        const val SORT_DATE = 4
        const val DEFAULT_STYLE = SORT_DATE
        fun getSortText(sortType: Int): String {
            return when (sortType) {
                SORT_TITLE -> "Title"
                SORT_TYPE -> "Media Type"
                SORT_TIMELINE -> "Timeline"
                SORT_DATE -> "Date Released"
                else -> "SortTypeNotFound"
            }
        }
        fun getAllStyles(): Array<Int>{
            return arrayOf(SORT_TITLE, SORT_TIMELINE, SORT_DATE)
        }
    }

    override fun compare(p0: MediaAndNotes?, p1: MediaAndNotes?): Int {
        if (p0 == null || p1 == null) {
            if (p0 == null && p1 == null) {
                return 0
            } else if (p0 == null) {
                return 1
            } else if (p1 == null) {
                return -1
            }
        }
        val compNum : Int = when (style){
            SORT_TITLE -> compareTitles(p0, p1)
            //SORT_TYPE -> FilterObject.getTextForType(p0.mediaItem.type).compareTo(FilterObject.getTextForType(p1.mediaItem.type))
            SORT_TIMELINE -> sign(p0.mediaItem.timeline - p1.mediaItem.timeline).toInt()
            SORT_DATE -> compareDates(p0.mediaItem, p1.mediaItem)
            else -> compareTitles(p0,p1)
        }
        return if (ascending){
            compNum
        }else{
            -compNum
        }
    }

    private fun compareTitles(p0: MediaAndNotes, p1: MediaAndNotes): Int{
        val title1 = p0.mediaItem.title
        val title2 = p1.mediaItem.title
        if (title1 == null || title2 == null) {
            if (title1 == null && title2 == null) {
                return 0
            } else if (title1 == null) {
                return 1
            } else if (title2 == null) {
                return -1
            }
        }
        return title1.compareTo(title2)
    }

    private fun compareDates(item1: MediaItem, item2: MediaItem): Int{
        if ((item1.date == null || item1.date == "") || (item2.date == null || item2.date == "")) {
            if ((item1.date == null || item1.date == "") && (item2.date == null || item2.date == "")) {
                return 0
            } else if (item1.date == null || item1.date == "") {
                return 1
            } else if (item2.date == null || item2.date == "") {
                return -1
            }
        }
        val splitDate1 = item1.date.split("/")
        val splitDate2 = item2.date.split("/")
        var compare = (splitDate1[2].toInt()-splitDate2[2].toInt())
        if (compare == 0){
            compare = (splitDate1[0].toInt()-splitDate2[0].toInt())
        }
        if (compare == 0){
            compare = (splitDate1[1].toInt()-splitDate2[1].toInt())
        }
        return compare
    }

    fun getText(): String{
        return getSortText(style)
    }
    fun isAscending(): Boolean{
        return ascending
    }

    override fun reversed(): SortStyle {
        return SortStyle(style, !ascending)
    }
}