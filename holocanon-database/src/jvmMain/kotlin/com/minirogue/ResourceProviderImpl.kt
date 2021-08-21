package com.minirogue

import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

internal class ResourceProviderImpl : ResourceProvider {
    override suspend fun getCsvResource(resource: ResourceProvider.CsvResources): Collection<Map<String, String>> =
        withContext(Dispatchers.IO) {
            try {
                val inputStreamReader = javaClass.classLoader.getResourceAsStream("csv/${resource.filename}")?.let {
                    InputStreamReader(it, StandardCharsets.UTF_8)
                }

                val csvReader = CSVReader(inputStreamReader)
                val header = csvReader.readNext()
                val entries = csvReader.readAll()
                entries.map { fullEntry ->
                    header.zip(fullEntry) { head: String, item: String -> head to item }.toMap()
                }.also { csvReader.close() }
            } catch (e: IOException) {
                println("exception with resource $resource: $e")
                emptyList()
            }
        }
}
