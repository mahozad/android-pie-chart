package ir.mahozad.android.compose

import androidx.compose.ui.geometry.Size
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SizeUtilComposeTest {

    @Nested inner class PieRadius {
        @Test fun calculatePieRadiusUnspecifiedSize() {
            val size = Size.Unspecified
            assertThrows<IllegalStateException> { calculatePieRadius(size) }
        }

        @Test fun calculatePieRadiusWidthAndHeightEqualToZero() {
            val size = Size.Zero
            val radius = calculatePieRadius(size)
            assertThat(radius).isEqualTo(0f)
        }

        @Test fun calculatePieRadiusWidthLargerThanHeight() {
            val size = Size(102f, 45f)
            val radius = calculatePieRadius(size)
            assertThat(radius).isEqualTo(22.5f)
        }

        @Test fun calculatePieRadiusWidthSmallerThanHeight() {
            val size = Size(10f, 45f)
            val radius = calculatePieRadius(size)
            assertThat(radius).isEqualTo(5f)
        }

        @Test fun calculatePieRadiusWidthEqualToHeight() {
            val size = Size(59f, 59f)
            val radius = calculatePieRadius(size)
            assertThat(radius).isEqualTo(29.5f)
        }
    }

    @Nested inner class StartAngles {
        @Test fun calculateStartAnglesWhenFractionListIsEmpty() {
            val startAngle = -90
            val fractions = listOf<Float>()
            val startAngles = calculateStartAngles(startAngle, fractions)
            assertThat(startAngles).isEqualTo(emptyList<Float>())
        }

        @Test fun calculateStartAnglesWith1SmallFraction() {
            val startAngle = -90
            val fractions = listOf(0.08f)
            val startAngles = calculateStartAngles(startAngle, fractions)
            assertThat(startAngles).isEqualTo(listOf(270f))
        }

        @Test fun calculateStartAnglesWith1LargeFraction() {
            val startAngle = -90
            val fractions = listOf(0.38f)
            val startAngles = calculateStartAngles(startAngle, fractions)
            assertThat(startAngles).isEqualTo(listOf(270f))
        }

        @Test fun calculateStartAnglesWith2Fractions() {
            val startAngle = -90
            val fractions = listOf(0.08f, 0.92f)
            val startAngles = calculateStartAngles(startAngle, fractions)
            assertThat(startAngles).isEqualTo(listOf(270f, 298.8f))
        }
    }
}
