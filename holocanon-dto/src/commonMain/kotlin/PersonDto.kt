import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PersonDto(@SerialName("person_id") val id: Long, val name: String)
