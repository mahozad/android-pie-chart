package io.github.mahozad

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.github.mahozad.piechart.R

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TEST", "In onCreate of TestActivity...")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}
