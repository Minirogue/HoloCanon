package com.minirogue.common.model

import kotlinx.serialization.Serializable

@Serializable
data class StarWarsMedia(
    val id: Long,
    val title: String,
    val type: MediaType,
    val imageUrl: String?,
    val releaseDate: String,
    val timeline: Float?,
    val description: String?,
    val series: String?,
    val number: Long?,
    val publisher: Company,
    val ranking: Long? = null,
)
