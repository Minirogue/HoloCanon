import android.util.Log
import com.minirogue.api.media.StarWarsMedia
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import javax.inject.Inject

 class GetMediaFromApiImpl @Inject constructor() : GetMediaFromApi {
    override suspend fun invoke(): List<StarWarsMedia> {
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.request("https://minirogue.github.io/holocanon-api/media.json")
        Log.d("holo-client", response.toString())
        return emptyList()
    }
}
