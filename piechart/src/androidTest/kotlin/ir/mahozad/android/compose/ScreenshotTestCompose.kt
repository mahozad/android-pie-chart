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
 * See README.md in the *androidTest* directory for documentations and notes.
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

        slices = listOf(SliceCompose(1f, Color.Green))

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
}
