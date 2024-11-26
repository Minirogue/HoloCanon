package com.minirogue.api.media

import com.holocanon.library.serialization.ext.internal.HolocanonJson
import com.minirogue.common.model.StarWarsMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.text.SimpleDateFormat
import java.util.Locale

private var inMemoryMedia: List<StarWarsMedia>? = null
private val holocanonJson = HolocanonJson()

internal suspend fun getFullMediaList(): List<StarWarsMedia> = inMemoryMedia
    ?: getMediaFromCsv().also { inMemoryMedia = it }

private suspend fun getMediaFromCsv(): List<StarWarsMedia> = withContext(Dispatchers.IO) {
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("media.csv")
    val reader = stream?.reader()
    val format = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .setIgnoreHeaderCase(true)
        .setTrim(true)
        .build()
    val csvParser = CSVParser(reader, format)
    val returnValue = mutableListOf<StarWarsMedia>()
    for (csvRecord in csvParser) {
        returnValue.add(
            StarWarsMedia(
                id = csvRecord.get("id").toLong(),
                title = csvRecord.get("title"),
                type = holocanonJson.decodeFromString("\"${csvRecord.get("type")}\""),
                imageUrl = csvRecord.get("image").ifEmpty { null },
                releaseDate = csvRecord.get("released").also { validateDate(it) },
                timeline = csvRecord.get("timeline").toFloatOrNull(),
                description = csvRecord.get("description").ifEmpty { null },
                series = csvRecord.get("series").ifEmpty { null },
                number = csvRecord.get("number").toLongOrNull(),
                publisher = holocanonJson.decodeFromString("\"${csvRecord.get("publisher")}\""),
                //     ranking = csvRecord.get("ranking").toLongOrNull()
            ),
        )
    }
    csvParser.close()
    returnValue
}

private fun validateDate(date: String) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    formatter.parse(date)
}
