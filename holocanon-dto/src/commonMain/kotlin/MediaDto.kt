import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MediaDto(
    @SerialName("media_id") val id: Long,
    val title: String,
    val type: String,
    val image: String? = null,
    val released: String,
    val timeline: Double? = null,
    val description: String? = null,
    val series: String? = null,
    val number: Long? = null,
    val publisher: String? = null,
    val author: Long? = null,
    val director: Long? = null,
    val illustrator: Long? = null,
)
