import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MediaTypeDto(@SerialName("media_type_id") val id: Long, val type: String)
