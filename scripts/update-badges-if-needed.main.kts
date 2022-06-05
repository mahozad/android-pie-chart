@file:JvmName("BadgeUpdater")
@file:CompilerOptions("-jvm-target", "11")

import java.io.File

val newKotlinVersion = args.single()
val readmeFile = File("README.md")
val readmeLines = readmeFile.readLines()
val kotlinLine = readmeLines.single { it.contains("[Kotlin]:") }
// See https://www.regular-expressions.info/lookaround.html
// and https://stackoverflow.com/a/2078953/8583692
val kotlinVersionRegex = Regex("""(?<=kotlin-)\d[.]\d+[.]\d+(-\w*)?(?=-)""")
val kotlinVersion = kotlinVersionRegex.find(kotlinLine)?.value
val newKotlinLine = kotlinLine.replace(kotlinVersionRegex, newKotlinVersion)
val newReadme = readmeLines
    .joinToString("\n")
    .replace(kotlinLine, newKotlinLine)
readmeFile.writeText(newReadme)

// Set outputs for GitHub action
val changed = newKotlinVersion != kotlinVersion
println("::set-output name=changed::$changed")
