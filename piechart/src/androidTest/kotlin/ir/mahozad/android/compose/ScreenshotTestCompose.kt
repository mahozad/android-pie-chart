package ir.mahozad.android.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.DisabledOnOs
import java.io.File

/**
 * These tests are used to visually inspect the chart to avoid any regressions.
 * Also, they are used to test whether changing chart properties work as expected.
 * This is a kind of end-to-end testing.
 *
 * See [this post](https://stackoverflow.com/a/69176420) for setup and how-to
 * of doing screenshot test for a composable.
 *
 * See [this article](https://medium.com/stepstone-tech/exploring-androidjunitrunner-filtering-options-df26d30b4f60)
 * which explains Android instrumentation runner arguments and how to use them.
 * To pass instrumentation arguments when running with Gradle see
 * [this post](https://stackoverflow.com/a/46183452).
 * To pass instrumentation arguments for an IDEA *Android Instrumentation Test* configuration,
 * click the *...* button in front of *Instrumentation arguments:* section.
 *
 * NOTE: We are using JUnit 4 tests because of the need for [createComposeRule].
 *  These tests can be run along with other JUnit 5 test classes with no problem.
 *
 * NOTE: Because JUnit 5 is built on Java 8 from the ground up, its instrumentation tests
 *  will only run on devices running Android 8.0 (API 26) or newer. Older phones will
 *  skip the execution of these tests completely, marking them as "ignored".
 *
 * NOTE: Make sure the device screen is on and the device is unlocked for the activity to go to resumed state.
 *
 * NOTE: If the repository size gets too big due to the history of screenshot files,
 *  we can delete older versions of the screenshots from VCS.
 *  See [this post](https://stackoverflow.com/q/26831494/).
 *
 * NOTE: The screenshot tests do not work the same on the GitHub emulator
 *  (even though the emulator device defined in ci.yml is the same as our main local device).
 *  So, we had to disable them on GitHub. Read on to see how.
 *
 * NOTE: Because the tests are instrumented tests and run in the JVM of the device,
 *  they cannot see the environment variables of the OS the device and the tests run in.
 *  So, annotations like [DisabledOnOs] or [DisabledIfEnvironmentVariable] do not
 *  have access to the OS environment variables and hence do not work as intended.
 *  The [Disabled] annotation works correctly, however.
 *
 * We used a custom BuildConfig field to check if we are running on CI.
 *  See the build script -> android -> buildTypes -> debug.
 *
 *  Another solution would be to use `InstrumentationRegistry.getArguments()`.
 *  See [this post](https://stackoverflow.com/a/46183452).
 *
 *  Another solution would be the following.
 *  See [this](https://stackoverflow.com/q/42675547) and [this](https://stackoverflow.com/q/40156906) SO posts.
 *  ```
 *  buildTypes {
 *    create("local") {
 *      initWith(buildTypes["debug"])
 *      buildConfigField("Boolean", "CI", "${System.getenv("CI") == "true"}")
 *      isDebuggable = true
 *    }
 *    testBuildType = "local"
 *  ```
 * With this solution, do not forget to change the build variant to "local" in the IDE.
 */
@Suppress("UsePropertyAccessSyntax")
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
@TestInstance(PER_CLASS)
class ScreenshotTestCompose {

    @get:Rule val composeTestRule = createComposeRule()

    val shouldSave = InstrumentationRegistry.getArguments().getString("shouldSave", "false").toBoolean()
    val shouldAssert = InstrumentationRegistry.getArguments().getString("shouldAssert", "true").toBoolean()

    @Test fun first() {
        val screenshotName = "screenshot"

        composeTestRule.setContent { PieChartPreview() }
        val node = composeTestRule.onRoot()
        val screenshot = node.captureToImage().asAndroidBitmap()

        if (shouldSave) {
            saveScreenshot(screenshot, screenshotName)
        }
        if (shouldAssert) {
            val reference = loadReferenceScreenshot(screenshotName)
            assertThat(screenshot.sameAs(reference))
                .withFailMessage { "Screenshots are not the same: $screenshotName.png" }
                .isTrue()
        }
    }

    /**
     * The screenshots are saved in /Android/data/ir.mahozad.android.test/files/Pictures
     * on the external storage of the device.
     *
     * Saving files on the device both requires WRITE permission in the manifest file and also
     * adb install options -g and -r. See the build script for more information.
     */
    private fun saveScreenshot(screenshot: Bitmap, name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(path, "$name.png")
        file.outputStream().use { stream ->
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }

    /**
     * NOTE: the *assets* directory was specified as an assets directory in the build script.
     *
     * Could also have saved the screenshots in *drawable* directory and loaded them like below.
     * See [this post](https://stackoverflow.com/a/9899056).
     * ```
     * val resourceId = ir.mahozad.android.test.R.drawable.myDrawableName
     * val reference = BitmapFactory.decodeResource(context.resources, resourceId)
     * ```
     */
    private fun loadReferenceScreenshot(name: String): Bitmap {
        val context = InstrumentationRegistry.getInstrumentation().context
        val reference = context.resources.assets.open("compose/$name.png").use {
            BitmapFactory.decodeStream(it)
        }
        return reference
    }
}
