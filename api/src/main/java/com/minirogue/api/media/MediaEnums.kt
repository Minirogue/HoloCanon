package com.minirogue.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    @SerialName("Movie")
    MOVIE,

    @SerialName("Novelization")
    NOVELIZATION,

    @SerialName("Original Novel")
    NOVEL,

    @SerialName("Video Game")
    GAME,

    @SerialName("Youth")
    YOUTH,

    @SerialName("Reference")
    REFERENCE,

    @SerialName("Comic Book")
    COMIC,

    @SerialName("TPB")
    TPB,

    @SerialName("TV Season")
    SEASON,

    @SerialName("TV Episode")
    EPISODE,

    @SerialName("Comic Adaptation")
    COMIC_ADAPTATION,

    @SerialName("TPB Adaptation")
    TPB_ADAPTATION,

    @SerialName("Short")
    SHORT,

    @SerialName("Audiobook")
    AUDIOBOOK;
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
    GOLDEN_BOOKS;
}
