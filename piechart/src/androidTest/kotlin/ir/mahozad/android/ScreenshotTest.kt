package ir.mahozad.android

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import androidx.test.uiautomator.UiDevice
import de.mannodermaus.junit5.ActivityScenarioExtension
import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import ir.mahozad.android.PieChart.Slice
import ir.mahozad.android.component.Alignment
import ir.mahozad.android.component.Wrapping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * These tests are used to visually inspect the chart to avoid any regressions.
 * Also, they are used to test whether changing chart properties work as expected.
 * This is a kind of end-to-end testing.
 *
 * Make sure the device screen is on and unlocked for the activity to go to resumed state.
 *
 * NOTE: If the repository size gets big due to the history of screenshot files,
 *  we can delete older versions of the screenshots from VCS.
 *  See [this post](https://stackoverflow.com/q/26831494/).
 *
 * NOTE: Because JUnit 5 is built on Java 8 from the ground up, its instrumentation tests
 *  will only run on devices running Android 8.0 (API 26) or newer. Older phones will
 *  skip the execution of these tests completely, marking them as "ignored".
 *
 * NOTE: The screenshot tests do not work the same on the GitHub emulator
 *  (even though the emulator device defined in ci.yml is the same as our main local device).
 *  So, we had to disable them on GitHub. Read on to see how.
 *
 * NOTE: Because the tests are instrumented tests and run in the JVM of the device,
 *  they cannot see the environment variables of the OS the device and the tests run in.
 *  So, annotations like [DisabledOnOs] or [DisabledIfEnvironmentVariable] do not
 *  have access to the OS environment variables and hence do not work as intended.
 *  The [Disabled] annotation works correctly.
 *
 *  We used a custom BuildConfig field to check if we are running on CI.
 *  See the build script -> android -> buildTypes -> debug.
 *
 *  Another solution would be to use `InstrumentationRegistry.getArguments()`.
 *  See [this post](https://stackoverflow.com/a/46183452).
 *
 *  Another solution would be the following.
 *  See [this](https://stackoverflow.com/q/42675547) and [this](https://stackoverflow.com/q/40156906) SO posts.
 *  ```
 *  buildTypes {
 *      create("local") {
 *          initWith(buildTypes["debug"])
 *          buildConfigField("Boolean", "CI", "${System.getenv("CI") == "true"}")
 *          isDebuggable = true
 *      }
 *      testBuildType = "local"
 *  ```
 *  With this solution, do not forget to change the build variant to "local" in the IDE.
 */
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
class ScreenshotTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<ScreenshotTestActivity>()
    lateinit var scenario: ActivityScenario<ScreenshotTestActivity>
    lateinit var device: UiDevice
    // See https://stackoverflow.com/a/46183452
    val shouldSave = InstrumentationRegistry.getArguments().getString("shouldSave", "false").toBoolean()

    @BeforeEach fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        scenario = scenarioExtension.scenario
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test fun chartShouldBeDisplayed() {
        compareScreenshots("screenshot-1") {}
    }

    @Test fun changeSlices() {
        compareScreenshots("screenshot-2") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW),
                Slice(0.5f, Color.GREEN)
            )
        }
    }

    @Test fun changeSlicesSuchThatChartLayoutChanges() {
        compareScreenshots("screenshot-3") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW),
                Slice(0.1f, Color.GREEN),
                Slice(0.1f, Color.MAGENTA),
                Slice(0.1f, Color.WHITE),
                Slice(0.1f, Color.GRAY),
                Slice(0.1f, Color.LTGRAY)
            )
        }
    }

    @Test fun changeSlicesTwice() {
        compareScreenshots("screenshot-4") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW),
                Slice(0.5f, Color.GREEN)
            )
            slices = listOf(
                Slice(0.2f, Color.YELLOW),
                Slice(0.5f, Color.GREEN),
                Slice(0.3f, Color.CYAN)
            )
        }
    }

    @Test fun changeHoleRatio() {
        compareScreenshots("screenshot-5") { holeRatio = 0.67f }
    }

    @Test fun changeStartAngle() {
        compareScreenshots("screenshot-6") { startAngle = 121 }
    }

    @Test fun changeOverlayRatio() {
        compareScreenshots("screenshot-7") { overlayRatio = 0.591f }
    }

    @Test fun changeOverlayAlpha() {
        compareScreenshots("screenshot-8") { overlayAlpha = 0.891f }
    }

    @Test fun changeGap() {
        compareScreenshots("screenshot-9") { gap = 15.dp }
    }

    @Test fun changeLabelsSize() {
        compareScreenshots("screenshot-10") { labelsSize = 17.sp }
    }

    @Test fun changeLabelIconsHeight() {
        compareScreenshots("screenshot-11") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW, labelIcon = R.drawable.ic_circle),
                Slice(0.5f, Color.GREEN)
            )
            labelIconsHeight = 27.dp
        }
    }

    @Test fun disableLegends() {
        compareScreenshots("screenshot-12") { isLegendEnabled = false }
    }

    @Test fun enableLegends() {
        compareScreenshots("screenshot-13") { isLegendEnabled = true }
    }

    @Test fun changeLegendsSize() {
        compareScreenshots("screenshot-14") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW, legend = "Test"),
                Slice(0.5f, Color.GREEN)
            )
            legendsSize = 20.sp
        }
    }

    @Test fun changeLegendsPercentageSize() {
        compareScreenshots("screenshot-15") { legendsPercentageSize = 20.sp }
    }

    @Test fun changeLegendsIconHeight() {
        compareScreenshots("screenshot-16") { legendIconsHeight = 20.dp }
    }

    @Test fun changeLegendsTitleAlignment() {
        compareScreenshots("screenshot-17") { legendsTitleAlignment = Alignment.START }
    }

    @Test fun changeLegendsAlignment() {
        compareScreenshots("screenshot-18") {
            legendsMargin = 50.dp // So they constitute multiple lines
            legendsAlignment = Alignment.END
        }
    }

    @Test fun changeLegendBoxAlignment() {
        compareScreenshots("screenshot-19") { legendBoxAlignment = Alignment.END }
    }

    @Test fun changeLegendsWrapping() {
        compareScreenshots("screenshot-20") {
            legendsMargin = 50.dp // So they constitute multiple lines
            legendsWrapping = Wrapping.CLIP
        }
    }

    @Test fun changeLegendsTitle() {
        compareScreenshots("screenshot-21") { legendsTitle = "Title set in the test" }
    }

    @Test fun changeLegendPosition() {
        compareScreenshots("screenshot-22") { legendPosition = PieChart.LegendPosition.TOP }
    }

    @Test fun changeLegendArrangement() {
        compareScreenshots("screenshot-23") { legendArrangement = PieChart.LegendArrangement.VERTICAL }
    }

    @Test fun changeLegendsMargin() {
        compareScreenshots("screenshot-24") { legendsMargin = 53.dp }
    }

    @Test fun changeLegendsColor() {
        compareScreenshots("screenshot-25") {
            slices = listOf(
                Slice(0.3f, Color.CYAN),
                Slice(0.2f, Color.YELLOW, legend = "Test"),
                Slice(0.5f, Color.GREEN)
            )
            legendsColor = Color.rgb(221, 116, 101)
        }
    }

    @Test fun changeLegendBoxBackgroundColor() {
        compareScreenshots("screenshot-26") {
            legendBoxBackgroundColor = Color.rgb(217, 106, 109)
        }
    }

    @Test fun changeLegendBoxMargin() {
        compareScreenshots("screenshot-27") { legendBoxMargin = 43.dp }
    }

    @Test fun changeLegendBoxPadding() {
        compareScreenshots("screenshot-28") { legendBoxPadding = 43.dp }
    }

    @Test fun changeLegendBoxBorder() {
        compareScreenshots("screenshot-29") {
            isLegendBoxBorderEnabled = true
            legendBoxBorder = 17.dp
        }
    }

    @Test fun changeLegendBoxBorderCornerRadius() {
        compareScreenshots("screenshot-30") {
            isLegendBoxBorderEnabled = true
            legendBoxBorderCornerRadius = 11.dp
        }
    }

    @Test fun changeLegendBoxBorderColor() {
        compareScreenshots("screenshot-31") {
            isLegendBoxBorderEnabled = true
            legendBoxBorder = 10.dp
            legendBoxBorderColor = Color.rgb(221, 180, 146)
        }
    }

    @Test fun changeLegendBoxBorderAlpha() {
        compareScreenshots("screenshot-32") {
            isLegendBoxBorderEnabled = true
            legendBoxBorder = 10.dp
            legendBoxBorderAlpha = 0.2f
        }
    }

    @Test fun changeLegendBoxBorderType() {
        compareScreenshots("screenshot-33") {
            isLegendBoxBorderEnabled = true
            legendBoxBorder = 4.dp
            legendBoxBorderType = PieChart.BorderType.DASHED
        }
    }

    @Test fun changeLegendBoxBorderDashArray() {
        compareScreenshots("screenshot-34") {
            isLegendBoxBorderEnabled = true
            legendBoxBorder = 4.dp
            legendBoxBorderType = PieChart.BorderType.DASHED
            legendBoxBorderDashArray = listOf(13.dp, 3.dp)
        }
    }

    @Test fun changeLegendIconsAlpha() {
        compareScreenshots("screenshot-35") {
            slices = listOf(
                Slice(0.3f, Color.CYAN, legendIconAlpha = null),
                Slice(0.2f, Color.YELLOW),
                Slice(0.5f, Color.GREEN)
            )
            legendIconsAlpha = 0.1f
        }
    }

    @Test fun changeLegendsTitleColor() {
        compareScreenshots("screenshot-36") {
            legendsTitleColor = Color.rgb(151, 109, 64)
        }
    }

    @Test fun changeLegendsTitleSize() {
        compareScreenshots("screenshot-37") {
            legendsTitleSize = 30.sp
        }
    }

    @Test fun disableLegendsPercentage() {
        compareScreenshots("screenshot-38") {
            isLegendsPercentageEnabled = false
        }
    }

    @Test fun enableLegendBoxBorder() {
        compareScreenshots("screenshot-39") {
            legendBoxBorder = 5.dp
            isLegendBoxBorderEnabled = true
        }
    }

    @Test fun changeLegendsPercentageColor() {
        compareScreenshots("screenshot-40") {
            legendsPercentageColor = Color.rgb(193, 224, 79)
        }
    }

    @Test fun changeLegendsPercentageMargin() {
        compareScreenshots("screenshot-41") {
            slices = listOf(
                Slice(0.3f, Color.CYAN, legend = "one"),
                Slice(0.2f, Color.YELLOW, legend = "two"),
                Slice(0.5f, Color.GREEN, legend = "three")
            )
            legendsPercentageMargin = 30.dp
        }
    }

    @Test fun changeLegendTitleMargin() {
        compareScreenshots("screenshot-42") {
            legendTitleMargin = 31.dp
        }
    }

    @Test fun changeLegendLinesMargin() {
        compareScreenshots("screenshot-43") {
            legendsMargin = 50.dp // So they constitute multiple lines
            legendLinesMargin = 31.dp
        }
    }

    @Test fun changeLegendIconsMargin() {
        compareScreenshots("screenshot-44") {
            legendIconsMargin = 31.dp
        }
    }

    @Test fun changeLabelsFont() {
        compareScreenshots("screenshot-45") {
            val newFont = ResourcesCompat.getFont(context, ir.mahozad.android.test.R.font.lobster_regular)
            labelsFont = newFont!!
        }
    }

    @Test fun changeCenterLabelFont() {
        compareScreenshots("screenshot-46") {
            isCenterLabelEnabled = true
            centerLabel = "Test center label"
            val newFont = ResourcesCompat.getFont(context, ir.mahozad.android.test.R.font.lobster_regular)
            centerLabelFont = newFont!!
        }
    }

    @Test fun changeCenterLabelIconHeight() {
        compareScreenshots("screenshot-47") {
            isCenterLabelEnabled = true
            centerLabelIcon = PieChart.DefaultIcons.CIRCLE
            centerLabel = "Test center label"
            centerLabelIconHeight = 33.dp
        }
    }

    @Test fun changeCenterLabelSize() {
        compareScreenshots("screenshot-48") {
            isCenterLabelEnabled = true
            centerLabel = "Test center label"
            centerLabelSize = 23.dp
        }
    }

    @Test fun changeCenterLabelIconMargin() {
        compareScreenshots("screenshot-49") {
            isCenterLabelEnabled = true
            centerLabelIcon = PieChart.DefaultIcons.CIRCLE
            centerLabel = "Test center label"
            centerLabelIconMargin = 23.dp
        }
    }

    @Test fun changeCenterLabelIconTint() {
        compareScreenshots("screenshot-50") {
            isCenterLabelEnabled = true
            centerLabelIcon = PieChart.DefaultIcons.CIRCLE
            centerLabel = "Test center label"
            centerLabelIconTint = Color.DKGRAY
        }
    }

    @Test fun changeCenterLabelAlpha() {
        compareScreenshots("screenshot-51") {
            isCenterLabelEnabled = true
            centerLabel = "Test center label"
            centerLabelAlpha = 0.23f
        }
    }

    @Test fun changeCenterLabelIconAlpha() {
        compareScreenshots("screenshot-52") {
            isCenterLabelEnabled = true
            centerLabelIcon = PieChart.DefaultIcons.CIRCLE
            centerLabel = "Test center label"
            centerLabelIconAlpha = 0.43f
        }
    }

    @Test fun enableCenterBackground() {
        compareScreenshots("screenshot-53") {
            isCenterBackgroundEnabled = true
        }
    }

    @Test fun changeCenterBackgroundColor() {
        compareScreenshots("screenshot-54") {
            isCenterBackgroundEnabled = true
            centerBackgroundColor = Color.rgb(238, 131, 98)
        }
    }

    @Test fun changeCenterBackgroundRatio() {
        compareScreenshots("screenshot-55") {
            isCenterBackgroundEnabled = true
            centerBackgroundRatio = 0.638f
        }
    }

    @Test fun changeCenterBackgroundAlpha() {
        compareScreenshots("screenshot-56") {
            isCenterBackgroundEnabled = true
            centerBackgroundColor = Color.rgb(238, 131, 98)
            centerBackgroundAlpha = 0.537f
        }
    }

    @Test fun changeLabelsOffset() {
        compareScreenshots("screenshot-57") {
            labelsOffset = 0.817f
        }
    }

    @Test fun changeLabelIconsMargin() {
        compareScreenshots("screenshot-58") {
            slices = listOf(
                Slice(0.3f, Color.CYAN, labelIcon = R.drawable.ic_circle),
                Slice(0.2f, Color.YELLOW, legend = "two"),
                Slice(0.5f, Color.GREEN, legend = "three")
            )
            labelIconsMargin = 39.dp
        }
    }

    @Test fun changeOutsideLabelsMargin() {
        compareScreenshots("screenshot-59") {
            labelType = PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
            outsideLabelsMargin = 39.dp
        }
    }

    @Test fun changeLabelType() {
        compareScreenshots("screenshot-60") {
            labelType = PieChart.LabelType.OUTSIDE
        }
    }

    @Test fun changeLabelsColor() {
        compareScreenshots("screenshot-61") {
            labelsColor = Color.rgb(234, 197, 90)
        }
    }

    @Test fun changeLabelIconsTint() {
        compareScreenshots("screenshot-62") {
            slices = listOf(
                Slice(0.3f, Color.CYAN, labelIcon = R.drawable.ic_circle),
                Slice(0.2f, Color.YELLOW, legend = "two"),
                Slice(0.5f, Color.GREEN, legend = "three")
            )
            labelIconsTint = Color.rgb(79, 161, 205)
        }
    }

    @Test fun changeSlicesPointer() {
        compareScreenshots("screenshot-63") {
            slicesPointer = PieChart.SlicePointer(34.dp, 19.dp, 0)
        }
    }

    @Test fun changeLabelIconsPlacement() {
        compareScreenshots("screenshot-64") {
            slices = listOf(
                Slice(0.3f, Color.CYAN, labelIcon = R.drawable.ic_circle),
                Slice(0.2f, Color.YELLOW, legend = "two"),
                Slice(0.5f, Color.GREEN, legend = "three")
            )
            labelIconsPlacement = PieChart.IconPlacement.TOP
        }
    }

    /**
     * FIXME: the name of the function is misleading. It also works in a saving
     *  mode in that it just saves the screenshot on device and skips the comparison.
     */
    private fun compareScreenshots(screenshotName: String, configure: PieChart.() -> Unit) {
        scenario.onActivity { activity ->
            activity.configureChart { chart ->
                chart.configure()
                val bitmap = takeScreenshot(chart, screenshotName, shouldSave)
                if (!shouldSave) {
                    val reference = loadReferenceScreenshot(screenshotName)
                    assertThat(bitmap.sameAs(reference))
                        .withFailMessage { "Screenshots are not the same: $screenshotName" }
                        .isTrue()
                }
            }
        }
    }

    /**
     * Saving files on the device both requires WRITE permission in the manifest file and also
     * adb install options -g and -r. See the build script for more information.
     *
     * The [Screenshot.capture] method can accept an [Activity] and take a screenshot of it as well.
     *
     * To manually capture a screenshot of the view and save it on the device, do like this:
     * ```
     * val bitmap = Bitmap.createBitmap(
     *     chart.width, /* chart.layoutParams.width */
     *     chart.height, /* chart.layoutParams.height */
     *     Bitmap.Config.ARGB_8888
     * )
     * val canvas = Canvas(bitmap)
     * chart.layout(chart.left, chart.top, chart.right, chart.bottom)
     * chart.draw(canvas)
     *
     * // Save the bitmap as PNG
     * val context = InstrumentationRegistry.getInstrumentation().context
     * val outputStream = File("${Environment.getExternalStorageDirectory()}/pic.png").outputStream()
     * // OR val outputStream = File("${context.filesDir}/pic.png").outputStream()
     * bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
     * ```
     */
    private fun takeScreenshot(view: View, name: String, shouldSave: Boolean): Bitmap {
        val screenCapture = Screenshot.capture(view)
        if (shouldSave) {
            screenCapture.name = name
            // Saves the screenshot in the device (in Pictures/Screenshots/).
            // The default processor will also append a UUID to the screenshot name.
            screenCapture.process()
        }
        return screenCapture.bitmap
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
        val reference = context.resources.assets.open("$name.png").use {
            BitmapFactory.decodeStream(it)
        }
        return reference
    }
}
