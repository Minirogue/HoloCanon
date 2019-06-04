package com.minirogue.starwarsmediatracker

import com.minirogue.starwarsmediatracker.database.MediaAndNotes
import com.minirogue.starwarsmediatracker.database.MediaItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.sign


class SortStyle(private val style: Int, private val ascending: Boolean) : Comparator<MediaAndNotes>{

    companion object{
        const val SORT_TITLE = 1
        const val SORT_TIMELINE = 2
        const val SORT_TYPE = 3
        const val SORT_DATE = 4
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
            return arrayOf(SORT_TITLE, SORT_TYPE, SORT_TIMELINE, SORT_DATE)
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
            SORT_TITLE -> p0.mediaItem.title.compareTo(p1.mediaItem.title)
            SORT_TYPE -> FilterObject.getTextForType(p0.mediaItem.type).compareTo(FilterObject.getTextForType(p1.mediaItem.type))
            SORT_TIMELINE -> sign(p0.mediaItem.timeline - p1.mediaItem.timeline).toInt()
            SORT_DATE -> compareDates(p0.mediaItem, p1.mediaItem)
            else -> p0.mediaItem.title.compareTo(p1.mediaItem.title)
        }
        return if (ascending){
            compNum
        }else{
            -compNum
        }
    }

    fun compareDates(item1: MediaItem, item2: MediaItem): Int{
        if ((item1.date == null || item1.date.equals("")) || (item2.date == null || item2.date.equals(""))) {
            if ((item1.date == null || item1.date.equals("")) && (item2.date == null || item2.date.equals(""))) {
                return 0
            } else if (item1.date == null || item1.date.equals("")) {
                return 1
            } else if (item2.date == null || item2.date.equals("")) {
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