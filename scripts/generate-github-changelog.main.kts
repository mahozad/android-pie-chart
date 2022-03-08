@file:JvmName("ChangelogGenerator")
@file:CompilerOptions("-jvm-target", "11")
@file:Repository("https://repo.maven.apache.org/maven2")
@file:Repository("https://jcenter.bintray.com")
@file:Repository("https://jitpack.io")
// @file:DependsOn("com.example:library:1.2.3")

import java.io.File

val inputFile = File("CHANGELOG.md")
val outputFile = File("changelog.txt")
val versionHeaderLineRegex = Regex(""".*v\d+\.\d+\.\d+.* \(\d{4}-\d{2}-\d{2}\)""")
val commitReferenceRegex = Regex("""\[`[\da-f]{8}`]\([^(]+[\da-f]{8}\)""")
val issueReferenceRegex = Regex("""\[#\d+]\([^(]+/[\d]+\)""")

val result = buildString {
    val releaseType = determineTypeOfThisRelease()
    val header = "This is a $releaseType release."
    val body = createReleaseBody()
    appendLine(header)
    appendLine()
    append(body)
}

outputFile.writeText(result)

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

fun getLastTwoVersionTags() = inputFile
    .readLines()
    .filter { it matches versionHeaderLineRegex }
    .take(2)
    .map { it.substringBetween("v", " ") }

fun createReleaseBody() = inputFile
    .readLines()
    .dropWhile { !it.matches(versionHeaderLineRegex) }
    .dropWhile { it.matches(versionHeaderLineRegex) }
    .takeWhile { !it.matches(versionHeaderLineRegex) }
    .joinToString(separator = "\n")
    .cleanReferences()

/**
 * Remove links for commit SHAs and issues and pull requests
 * as GitHub automatically converts them to links.
 * Note that the GitHub links are visually different and also,
 * when hovering over them with mouse, a popup shows the commit/issue information.
 */
fun String.cleanReferences(): String {
    fun String.cleanCommitReferences() = replace(commitReferenceRegex) {
        it.value.substringBetween("[`", "`]")
    }
    fun String.cleanIssueReferences() = replace(issueReferenceRegex) {
        it.value.substringBetween("[", "]")
    }
    return cleanCommitReferences().cleanIssueReferences()
}

fun String.substringBetween(start: String, end: String) = substringAfter(start).substringBefore(end)
