<div align="center">

![Preview image](preview.svg)

</div>

# NOTE: This library is still in initial phases of development

### build.gradle[.kts]
```groovy
implementation("ir.mahozad.android:pie-chart:0.2.0")
```

### layout.xml
```xml
<ir.mahozad.android.PieChart
    android:id="@+id/pieChart"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:holeRatio="0.3"
    app:gap="8dp" />
```

### activity.kt
```kotlin
val pieChart = findViewById<PieChart>(R.id.pieChart)
pieChart.slices = listOf(
    PieChart.Slice(0.2f, Color.BLUE),
    PieChart.Slice(0.4f, Color.MAGENTA),
    PieChart.Slice(0.3f, Color.YELLOW),
    PieChart.Slice(0.1f, Color.CYAN),
)
```
