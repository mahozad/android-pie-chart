package ir.mahozad.android.compose

import androidx.compose.ui.graphics.Color
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import de.mannodermaus.junit5.ActivityScenarioExtension
import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.RegisterExtension

@Suppress("UsePropertyAccessSyntax")
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
@TestInstance(PER_CLASS)
class ScreenshotTestView {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<ScreenshotComposeViewTestActivity>()
    lateinit var scenario: ActivityScenario<ScreenshotComposeViewTestActivity>

    val shouldSave = InstrumentationRegistry.getArguments().getString("shouldSave", "false").toBoolean()
    val shouldAssert = InstrumentationRegistry.getArguments().getString("shouldAssert", "true").toBoolean()

    @BeforeEach fun setUp() {
        scenario = scenarioExtension.scenario
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test fun viewShouldBeDisplayedCorrectly() {
        val screenshotName = "screenshot-view-1"
        scenario.onActivity { activity ->
            activity.configureChart {
                val screenshot = drawToBitmap()
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
        }
    }

    @Test fun changeViewProperties() {
        val screenshotName = "screenshot-view-2"
        lateinit var chart: PieChartView
        scenario.onActivity { activity ->
            activity.configureChart {
                chart = this
                slices = listOf(SliceCompose(1f, Color.Red))
                holeRatio = 0.19f
            }
        }
        Thread.sleep(100) // FIXME: Because the below statement didn't capture the new state
        val screenshot = chart.drawToBitmap()
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
}
