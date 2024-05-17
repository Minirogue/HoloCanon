package com.minirogue.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
