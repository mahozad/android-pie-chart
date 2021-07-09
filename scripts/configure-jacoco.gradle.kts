/* See https://medium.com/@ranjeetsinha/jacoco-with-kotlin-dsl-f1f067e42cd0 */

val minimumRequiredCoverage = 0.35

val fileFilter = setOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*\$Lambda$*.*", // Jacoco can not handle several "$" in class name.
    "**/*\$inlined$*.*" // Kotlin specific, Jacoco can not handle several "$" in class name.
)

val classDirectoriesTree = fileTree(buildDir) {
    include(
        "**/classes/**/main/**",
        "**/intermediates/classes/debug/**",
        "**/intermediates/javac/debug/*/classes/**", // Android Gradle Plugin 3.2.x support.
        "**/tmp/kotlin-classes/debug/**"
    )
    exclude(fileFilter)
}

val sourceDirectoriesTree = fileTree(buildDir) {
    include(
        "src/main/java/**",
        "src/main/kotlin/**",
        "src/debug/java/**",
        "src/debug/kotlin/**"
    )
}

val executionDataTree = fileTree(buildDir) {
    include(
        "outputs/code_coverage/**/*.ec",
        "jacoco/jacocoTestReportDebug.exec",
        "jacoco/testDebugUnitTest.exec",
        "jacoco/test.exec"
    )
}

fun JacocoReportsContainer.reports() {
    html.isEnabled = true
    xml.isEnabled = true // Needed by Codecov
    html.destination = file("${buildDir}/reports/jacoco/jacocoTestReport/html")
    xml.destination = file("${buildDir}/reports/jacoco/jacocoTestReport/report.xml")
}

fun JacocoCoverageVerification.setDirectories() {
    sourceDirectories.setFrom(sourceDirectoriesTree)
    classDirectories.setFrom(classDirectoriesTree)
    executionData.setFrom(executionDataTree)
}

fun JacocoReport.setDirectories() {
    sourceDirectories.setFrom(sourceDirectoriesTree)
    classDirectories.setFrom(classDirectoriesTree)
    executionData.setFrom(executionDataTree)
}

tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    group = "verification"
    description = "Code coverage report for unit tests."
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    reports {
        reports()
    }
    setDirectories()
}

tasks.register<JacocoCoverageVerification>("jacocoAndroidCoverageVerification") {
    group = "verification"
    description = "Code coverage verification for Unit tests."
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = minimumRequiredCoverage.toBigDecimal()
            }
        }
    }
    setDirectories()
}
