@file:JvmName("ChangelogGenerator")
@file:CompilerOptions("-jvm-target", "11")
@file:Repository("https://jcenter.bintray.com")
// @file:DependsOn("com.example:library:1.2.3")

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

val versionLineRegex = Regex(""".*v\d+\.\d+\.\d+.* \(\d{4}-\d{2}-\d{2}\)""")
val outputPath = Path.of("changelog.txt")

val result = buildString {
    val releaseType = determineTypeOfThisRelease()
    val header = "This is a $releaseType release."
    val body = createReleaseBody()
    appendLine(header)
    appendLine()
    append(body)
}

Files.writeString(outputPath, result)

fun determineTypeOfThisRelease(): String {
    val (new, old) = getLastTwoVersionTags()
    val (newMajor, newMinor, _) = new.split(".")
    val (oldMajor, oldMinor, _) = old.split(".")
    return when {
        newMajor == "0" && newMinor > oldMinor -> "major"
        newMajor > oldMajor -> "major"
        newMinor > oldMinor -> "minor"
        else -> "patch"
    }
}

fun getLastTwoVersionTags() = Files
    .lines(Path.of("CHANGELOG.md"))
    .filter { it.matches(versionLineRegex) }
    .limit(2)
    .map { it.substringAfter("v") }
    .map { it.substringBefore(" ") }
    .collect(Collectors.toList())

fun createReleaseBody() = Files
    .lines(Path.of("CHANGELOG.md"))
    .dropWhile { !it.matches(versionLineRegex) }
    .dropWhile { it.matches(versionLineRegex) }
    .takeWhile { !it.matches(versionLineRegex) }
    .collect(Collectors.toList())
    .joinToString(separator = "\n")
