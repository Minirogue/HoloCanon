package com.minirogue.api.media

import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetMediaTest {
    @Test
    fun `ensure all media can be properly read from the CSV`() {
        runBlocking { getFullMediaList() }
    }
}
