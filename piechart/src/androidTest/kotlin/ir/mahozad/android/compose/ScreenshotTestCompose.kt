package ir.mahozad.android.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.DisabledOnOs

/**
 * These tests are used to visually inspect the chart to avoid any regressions.
 * Also, they are used to test whether changing chart properties work as expected.
 * This is a kind of end-to-end testing.
 *
 * For a complete and detailed answer about how to setup screenshot tests see
 * [this post](https://stackoverflow.com/a/69259011) for View and
 * [this post](https://stackoverflow.com/a/69176420) for Compose.
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
 * NOTE: By default `composeTestRule.mainClock.autoAdvance` is `true` so the
 *  screenshots are captured when the animations are finished and the compose is idle.
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
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
class ScreenshotTestCompose {

    @get:Rule val composeTestRule = createComposeRule()

    val shouldSave = InstrumentationRegistry.getArguments().getString("shouldSave", "false").toBoolean()
    val shouldAssert = InstrumentationRegistry.getArguments().getString("shouldAssert", "true").toBoolean()

    @Test fun chartShouldBeDisplayed() {
        val screenshotName = "screenshot-1"
        composeTestRule.setContent { PieChartCompose() }
        composeTestRule
            .takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test fun changeSlices() {
        val screenshotName = "screenshot-2"
        var slices by mutableStateOf(defaultSlices)
        composeTestRule.setContent { PieChartCompose(pieChartData = slices) }

        slices = listOf(Slice(1f, Color.Green))

        composeTestRule
            .takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test fun changeHoleRatio() {
        val screenshotName = "screenshot-3"
        composeTestRule.setContent { PieChartCompose(holeRatio = 0.11f) }
        composeTestRule
            .takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test fun changeOverlayRatio() {
        val screenshotName = "screenshot-4"
        composeTestRule.setContent { PieChartCompose(overlayRatio = 0.37f) }
        composeTestRule
            .takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test fun changeSlicesAnimation() {
        var slices = listOf(
            Slice(0.3f, Color.Green),
            Slice(0.7f, Color.Red),
        )
        composeTestRule.setContent { PieChartCompose(overlayRatio = 0.37f, holeRatio = 0f, pieChartData = slices) }
        slices = listOf(Slice(1f, Color.Green))
        // var slices by mutableStateOf(
        //     listOf(
        //         Slice(0.3f, Color.Green),
        //         Slice(0.7f, Color.Red),
        //     )
        // )
        // composeTestRule.setContent { PieChartCompose(overlayRatio = 0.37f, holeRatio = 0f, pieChartData = slices) }
        // composeTestRule.mainClock.autoAdvance = false
        // slices = listOf(Slice(1f, Color.Green))
        // for (i in 1..30) {
        //     composeTestRule.mainClock.advanceTimeBy(15)
        //     composeTestRule.takeScreenshot().saveIfNeeded(true, "/anim/frame$i")
        // }
    }
}
