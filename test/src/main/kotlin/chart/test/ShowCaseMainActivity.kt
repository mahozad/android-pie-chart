package chart.test

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlin.reflect.KClass

class ShowCaseMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { context.start(ShowcaseComposeActivity::class) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_compose_color))
                ) {
                    Text(stringResource(id = R.string.button_compose_title))
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { context.start(ShowcaseViewActivity::class) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_view_color))
                ) {
                    Text(stringResource(id = R.string.button_view_title))
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { context.start(ShowcaseLegacyActivity::class) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.button_legacy_color))
                ) {
                    Text(
                        stringResource(id = R.string.button_legacy_title),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }

    private fun <T : Activity> Context.start(kClass: KClass<T>) =
        startActivity(Intent(this, kClass.java))
}
