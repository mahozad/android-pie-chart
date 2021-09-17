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
}
