import java.io.File

// Before running the script, move the new screenshots to the androidTest/assets/ directory.
// This script deletes the old version of the screenshots and renames the new ones.
// Refer to ScreenshotTest.kt class documentations for more information.

val screenshotsDirectory = File("piechart/src/androidTest/assets")
val screenshotNameRegex = Regex("""screenshot-\d+\.png""")

screenshotsDirectory
    .walk()
    .filter { it.name matches screenshotNameRegex }
    .forEach(File::delete)

screenshotsDirectory
    .listFiles()
    ?.forEach {
        val screenshotNumber = it.name.substringAfter("screenshot-").substringBefore("-")
        val newName = "screenshot-$screenshotNumber.${it.extension}"
        it.renameTo(File("$screenshotsDirectory/$newName"))
    }
