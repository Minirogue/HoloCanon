import com.minirogue.api.media.StarWarsMedia

interface GetMediaFromApi {
    suspend operator fun invoke(): List<StarWarsMedia>
}