package com.minirogue.api.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MediaType(val legacyId: Int) {
    @SerialName("Movie")
    MOVIE(1),

    @SerialName("Novelization")
    NOVELIZATION(2),

    @SerialName("Original Novel")
    NOVEL(3),

    @SerialName("Video Game")
    GAME(4),

    @SerialName("Youth")
    YOUTH(5),

    @SerialName("Reference")
    REFERENCE(6),

    @SerialName("Comic Book")
    COMIC(7),

    @SerialName("TPB")
    TPB(8),

    @SerialName("TV Season")
    SEASON(9),

    @SerialName("TV Episode")
    EPISODE(10),

    @SerialName("Comic Adaptation")
    COMIC_ADAPTATION(11),

    @SerialName("TPB Adaptation")
    TPB_ADAPTATION(12),

    @SerialName("Short")
    SHORT(13),

    @SerialName("Audiobook")
    AUDIOBOOK(14);

    fun getSerialName() = MediaType.serializer().descriptor.getElementName(ordinal)
    companion object {
        fun getFromLegacyId(legacyId: Int): MediaType? = entries.firstOrNull { it.legacyId == legacyId }
    }
}

@Serializable
enum class Company {
    @SerialName("IDW")
    IDW,

    @SerialName("Marvel")
    MARVEL,

    @SerialName("Disney")
    DISNEY,

    @SerialName("Del Rey Books")
    DEL_REY,

    @SerialName("Random House")
    RANDOM_HOUSE,

    @SerialName("20th Century Fox")
    FOX,

    @SerialName("Disney-Lucasfilm Press")
    DISNEY_LUCASFILMS,

    @SerialName("Warner Bros.")
    WARNER_BROS,

    @SerialName("Cartoon Network")
    CARTOON_NETWORK,

    @SerialName("Netflix")
    NETFLIX,

    @SerialName("Electronic Arts")
    ELECTRONIC_ARTS,

    @SerialName("Golden Books")
    GOLDEN_BOOKS,

    @SerialName("Random House Audio")
    RANDOM_HOUSE_AUDIO,

    @SerialName("Random House Worlds")
    RANDOM_HOUSE_WORLDS;
}
