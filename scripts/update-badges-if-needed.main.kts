@file:JvmName("BadgeUpdater")
@file:CompilerOptions("-jvm-target", "11")

import java.io.File

val newKotlinVersion = args.single()
val readmeFile = File("README.md")
val readmeLines = readmeFile.readLines()
val kotlinLine = readmeLines.single { it.contains("[Kotlin]:") }
// See https://www.regular-expressions.info/lookaround.html
// and https://youtu.be/54WEfLKtCGk?t=1086
// and https://stackoverflow.com/a/2078953/8583692
val kotlinVersionRegex = Regex("""(?<=kotlin-)\d[.]\d+[.]\d+(-\w*)?(?=-)""")
val kotlinVersion = kotlinVersionRegex.find(kotlinLine)?.value
val newKotlinLine = kotlinLine.replace(kotlinVersionRegex, newKotlinVersion)
val newReadme = readmeLines
    .joinToString("\n")
    .replace(kotlinLine, newKotlinLine)
    .plus("\n")
readmeFile.writeText(newReadme)
// Set output for GitHub Actions
val changed = newKotlinVersion != kotlinVersion
println("::set-output name=isChanged::$changed")
