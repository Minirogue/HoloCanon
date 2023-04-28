package filters

interface GetFilter {
    suspend operator fun invoke(id: Int, typeId: Int): MediaFilter?
}