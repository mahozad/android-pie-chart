@file:JvmName("ChangelogGenerator")
@file:CompilerOptions("-jvm-target", "11")
@file:Repository("https://jcenter.bintray.com")
// @file:DependsOn("com.example:library:1.2.3")

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

val versionLineRegex = Regex(""".*v\d+\.\d+\.\d+.* \(\d{4}-\d{2}-\d{2}\)""")
val outputPath: Path = Path.of("changelog.txt")

val result = buildString {
    val releaseType = determineTypeOfThisRelease()
    val header = "This is a $releaseType release."
    val body = createReleaseBody()
    val cleanedBody = cleanTheBody(body)
    appendLine(header)
    appendLine()
    append(cleanedBody)
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

/**
 * Remove links for commit SHAs and issues and pull requests as GitHub
 * automatically converts them to links.
 * Note that the GitHub links are different in appearance and also in that,
 * when hovering over them with mouse, a popup shows the commit information.
 *
 * Also, re-insert the links to version diffs.
 */
fun cleanTheBody(body: String) = body
    .replace(Regex("""\[[\da-f]{8}]""")) { it.value.removeSurrounding("[", "]") }
    .replace(Regex("""\[#\d+]""")) { it.value.removeSurrounding("[", "]") }
    .replace(Regex("""\[.*]""")) {
        val (owner, repo) = System.getenv("GITHUB_REPOSITORY").split("/")
        val (new, old) = getLastTwoVersionTags()
        "${it.value}(https://github.com/$owner/$repo/compare/v$old...v$new)"
    }
