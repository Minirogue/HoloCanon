import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CompanyDto(@SerialName("company_id") val id: Long, val name: String)
