package ir.mahozad.android

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import androidx.test.uiautomator.UiDevice
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * These tests are used to just visually inspect the chart to avoid any regressions.
 *
 * Make sure the device screen is on and unlocked for the activity to go to resumed state.
 */
/*
Didn't work:
@DisabledIfEnvironmentVariable(
    named = "CI",
    matches = "true",
    disabledReason = "Because failed on the emulator used in the GitHub action"
)
*/
@Disabled
class ScreenshotTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()
    lateinit var scenario: ActivityScenario<TestActivity>
    lateinit var device: UiDevice

    @BeforeEach fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        scenario = scenarioExtension.scenario
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test fun theScreenshotsShouldBeTheSame(scenario: ActivityScenario<TestActivity>) {
        // Get bitmap from a view
        // val v = PieChart(context)
        // val b = Bitmap.createBitmap(
        //     v.getLayoutParams().width,
        //     v.getLayoutParams().height,
        //     Bitmap.Config.ARGB_8888
        // )
        // val c = Canvas(b)
        // v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom())
        // v.draw(c)
        // return b

        // Convert bitmap to PNG
        // bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream);

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resourceId = ir.mahozad.android.test.R.drawable.temp
        val saved = BitmapFactory.decodeResource(context.resources, resourceId)

        scenario.onActivity { activity ->
            activity.configureChart { chart ->
                chart.slices = emptyList()
            }
            val bitmap = takeScreenshot(activity)
            assertThat(bitmap.sameAs(saved)).isTrue()
        }
    }

    /**
     * Saving files on the device both requires WRITE permission in the manifest file and also
     * adb install options -g and -r. See the build script for more information.
     */
    private fun takeScreenshot(activity: Activity): Bitmap {
        val screenCapture = Screenshot.capture(activity)
        // screenCapture.name = "screenShotName"
        // screenCapture.process() // Saves the screenshot on device
        return screenCapture.bitmap
    }
}
