package chart.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlin.reflect.KClass

class DemoMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ComposeButton(context)
                Spacer(Modifier.height(24.dp))
                ViewButton(context)
                Spacer(Modifier.height(24.dp))
                LegacyButton(context)
            }
        }
    }

    @Composable fun ComposeButton(context: Context) = Button(
        onClick = { context.start(ComposeDemoActivity::class) },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_compose_color))
    ) {
        Text(stringResource(id = R.string.button_compose_title))
    }

    @Composable fun ViewButton(context: Context) = Button(
        onClick = { context.start(ViewDemoActivity::class) },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_view_color))
    ) {
        Text(stringResource(id = R.string.button_view_title))
    }

    @Composable fun LegacyButton(context: Context) = Button(
        onClick = { context.start(LegacyDemoActivity::class) },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_legacy_color))
    ) {
        Text(
            stringResource(id = R.string.button_legacy_title),
            textDecoration = TextDecoration.LineThrough
        )
    }

    private fun <T : Activity> Context.start(kClass: KClass<T>) =
        startActivity(Intent(this, kClass.java))
}
