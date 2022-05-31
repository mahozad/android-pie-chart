package ir.mahozad.android.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.platform.app.InstrumentationRegistry
import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import org.junit.jupiter.api.Test

/**
 * See README.md in the *androidTest* directory for documentations and notes.
 */
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
class ScreenshotTestCompose {

    val shouldSave = InstrumentationRegistry.getArguments().getString("shouldSave", "false").toBoolean()
    val shouldAssert = InstrumentationRegistry.getArguments().getString("shouldAssert", "true").toBoolean()

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun chartShouldBeDisplayed() = runComposeUiTest {
        val screenshotName = "screenshot-1"
        setContent { PieChartCompose() }
        takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun changeSlices() = runComposeUiTest {
        val screenshotName = "screenshot-2"
        var slices by mutableStateOf(defaultSlices)
        setContent { PieChartCompose(pieChartData = slices) }

        slices = listOf(SliceCompose(1f, Color.Green))

        takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun changeHoleRatio() = runComposeUiTest {
        val screenshotName = "screenshot-3"
        setContent { PieChartCompose(holeRatio = 0.11f) }
        takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun changeOverlayRatio() = runComposeUiTest {
        val screenshotName = "screenshot-4"
        setContent { PieChartCompose(overlayRatio = 0.37f) }
        takeScreenshot()
            .saveIfNeeded(shouldSave, screenshotName)
            .assertIfNeeded(shouldAssert, screenshotName)
    }
}
