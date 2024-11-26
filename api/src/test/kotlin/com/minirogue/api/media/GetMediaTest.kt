package com.minirogue.api.media

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMediaTest {
    @Test
    fun `ensure all media can be properly read from the CSV`() = runTest {
        // Arrange

        // Act
        getFullMediaList()

        // Assert
        // Act step shouldn't fail
    }

    @Test
    fun `ensure all media has cover image links`() = runTest {
        // Arrange
        val imageAddresses = getFullMediaList().associate { it.id to it.imageUrl }

        // Act
        val missingImages: Set<Long> = imageAddresses.filterValues { it == null }.keys

        // Assert
        assertTrue("The following ids are missing images: $missingImages", missingImages.isEmpty())
    }
}
