package ir.mahozad.android.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

/**
 * The screenshots are saved in /Android/data/ir.mahozad.android.test/files/Pictures
 * on the external storage of the device.
 *
 * Saving files on the device both requires WRITE permission in the manifest file and also
 * adb install options -g and -r. See the build script for more information.
 */
fun saveScreenshot(screenshot: Bitmap, name: String) {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(path, "$name.png")
    file.outputStream().use { stream ->
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
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
fun loadReferenceScreenshot(name: String): Bitmap {
    val context = InstrumentationRegistry.getInstrumentation().context
    val reference = context.resources.assets.open("compose/$name.png").use {
        BitmapFactory.decodeStream(it)
    }
    return reference
}
