import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SeriesDto(@SerialName("series_id") val id: Long, val name: String)
