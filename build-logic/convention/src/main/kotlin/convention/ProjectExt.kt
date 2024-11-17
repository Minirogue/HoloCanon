package convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneOffset
import java.util.Properties

internal val Project.libs
    get(): VersionCatalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
internal val Project.javaLibVersion
    get(): String = libs.findVersion("java").get().toString()

fun getDateAsVersionName(): String {
    val now = Instant.now().atOffset(ZoneOffset.UTC)
    return "${now.year % 100}.${now.monthValue}.${now.dayOfMonth}"
}
