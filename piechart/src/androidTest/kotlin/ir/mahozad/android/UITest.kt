package ir.mahozad.android

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import de.mannodermaus.junit5.ActivityScenarioExtension
import ir.mahozad.android.test.R
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * These tests check the final and real-world appearance of the chart in an activity
 * and also check the chart interactivity (like clicking etc.).
 *
 * See README.md in the *androidTest* directory for documentations and notes.
 */
@Disabled("Could not run in CI emulator")
class UITest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<ScreenshotTestActivity>()
    lateinit var scenario: ActivityScenario<ScreenshotTestActivity>
    lateinit var device: UiDevice

    @BeforeEach fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        scenario = scenarioExtension.scenario
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test fun theChartShouldBeDisplayed(scenario: ActivityScenario<ScreenshotTestActivity>) {
        Espresso
            .onView(withId(R.id.screenshotTestPieChart))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}
