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
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * These tests check the final and real-world appearance of the chart in an activity
 * and also check the chart interactivity (like clicking etc.).
 *
 * Make sure the device screen is on and unlocked for the activity to go to resumed state.
 *
 * NOTE: Because JUnit 5 is built on Java 8 from the ground up, its instrumentation tests
 *       will only run on devices running Android 8.0 (API 26) or newer. Older phones will
 *       skip the execution of these tests completely, marking them as "ignored".
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
class UITest {

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

    @Test fun theChartShouldBeDisplayed(scenario: ActivityScenario<TestActivity>) {
        Espresso
            .onView(withId(R.id.testPieChart))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}