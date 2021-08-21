package com.minirogue

import CompanyDto
import MediaDto
import MediaTypeDto
import PersonDto
import SeriesDto

internal fun Person.toAuthorDto(): PersonDto = PersonDto(id, name)
internal fun Company.toCompanyDto(): CompanyDto = CompanyDto(id, name)
internal fun Series.toSeriesDto(): SeriesDto = SeriesDto(id, name)
internal fun Media_type.toMediaTypeDto(): MediaTypeDto = MediaTypeDto(id, type)
internal fun Media.toMediaDto(): MediaDto = MediaDto(
    id = id,
    title = title,
    type = type,
    image = image,
    released = released,
    timeline = timeline,
    description = description,
    series = series,
    number = number,
    publisher = publisher,
    author = author,
    director = director,
    illustrator = illustrator
)
