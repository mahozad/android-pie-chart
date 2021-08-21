import java.io.File

// Before running the script, move the new screenshots to the androidTest/assets/ directory.
// This script deletes the old version of the screenshots and renames the new ones.
// Refer to ScreenshotTest.kt class documentations for more information.

val screenshotsDirectory = "piechart/src/androidTest/assets"

File(screenshotsDirectory)
    .walk()
    .filter { it.name.matches(Regex("""screenshot-\d+\.png""")) }
    .forEach { it.delete() }

File(screenshotsDirectory)
    .listFiles()
    ?.forEach {
        val screenshotNumber = it.name.substringAfter("screenshot-").substringBefore("-")
        val newName = "screenshot-$screenshotNumber.${it.extension}"
        it.renameTo(File("$screenshotsDirectory/$newName"))
    }
