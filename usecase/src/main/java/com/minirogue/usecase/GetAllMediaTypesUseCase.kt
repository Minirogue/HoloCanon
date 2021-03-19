package com.minirogue.usecase

import com.minirogue.starwarscanontracker.core.model.repository.MediaTypeRepository
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class GetAllMediaTypesUseCase @Inject constructor(private val mediaTypeRepo: MediaTypeRepository) {

    /**
     * WARNING: THIS IS NOT THREAD SAFE.
     */
    operator fun invoke(): List<MediaType> = mediaTypeRepo.getAllTypes()
}
