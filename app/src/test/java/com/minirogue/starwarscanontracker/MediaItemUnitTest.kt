package com.minirogue.starwarscanontracker

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemUnitTest {


    //Not the most useful unit test, but this is good to demonstrate how unit tests work for myself
    @Test
    fun toString_Works(){
        val item = MediaItem()
        item.title = "a very interesting title"
        assertEquals("a very interesting title",item.toString())
    }

}
