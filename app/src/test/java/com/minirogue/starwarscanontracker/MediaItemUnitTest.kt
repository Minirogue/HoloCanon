package com.minirogue.starwarscanontracker

import com.minirogue.starwarscanontracker.database.MediaItem
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemUnitTest {

    @Test
    fun toString_Works(){
        val item = MediaItem()
        item.title = "a very interesting title"
        assertEquals("a very interesting title",item.toString())
    }

}