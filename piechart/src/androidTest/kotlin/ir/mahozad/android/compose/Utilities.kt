@file:Suppress("UsePropertyAccessSyntax")

package ir.mahozad.android.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import java.io.File

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.takeScreenshot() = onRoot().captureToImage().asAndroidBitmap()

fun Bitmap.saveIfNeeded(shouldSave: Boolean, name: String): Bitmap {
    if (shouldSave) save(name)
    return this
}

fun Bitmap.assertIfNeeded(shouldCompare: Boolean, screenshotName: String) {
    if (shouldCompare) assert(screenshotName)
}

/**
 * The screenshots are saved in /Android/data/ir.mahozad.android.test/files/Pictures
 * on the external storage of the device.
 *
 * Saving files on the device both requires WRITE permission in the manifest file and also
 * adb install options -g and -r. See the build script for more information.
 */
private fun Bitmap.save(name: String) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(path, "$name.png")
    file.outputStream().use { stream ->
        compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
}

private fun Bitmap.assert(screenshotName: String) {
    val reference = loadReferenceScreenshot(screenshotName)
    assertThat(this.sameAs(reference))
        .withFailMessage { "Screenshots are not the same: $screenshotName.png" }
        .isTrue()
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
    val assets = context.resources.assets
    return assets.open("compose/$name.png").use { stream ->
        BitmapFactory.decodeStream(stream)
    }
}
