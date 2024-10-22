package filters

import filters.model.MediaFilter

interface GetFilter {
    suspend operator fun invoke(id: Int, typeId: Int): MediaFilter?
}
