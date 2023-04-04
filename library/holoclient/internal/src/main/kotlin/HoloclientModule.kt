import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface HoloclientModule {
    @Binds
    fun bindGetMediaFromApi(getMediaFromApiImpl: GetMediaFromApiImpl): GetMediaFromApi
}
