package chart.test

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                Button(onClick = { context.start(ShowcaseComposeActivity::class) }) {
                    Text("Compose version")
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = { context.start(ShowcaseViewActivity::class) }) {
                    Text("View version")
                }
            }
        }
    }

    private fun <T : Activity> Context.start(kClass: KClass<T>) =
        startActivity(Intent(this, kClass.java))
}
