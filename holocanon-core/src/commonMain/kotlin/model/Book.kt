package model

import kotlinx.serialization.Serializable

@Serializable
data class Book(val id: Long, val title: String)
