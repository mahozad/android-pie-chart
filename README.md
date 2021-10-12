[comment]: <> ([![Build Status]&#40;https://www.travis-ci.com/mahozad/android-pie-chart.svg?branch=master&#41;]&#40;https://www.travis-ci.com/mahozad/android-pie-chart&#41;)
[comment]: <> (![Dependencies]&#40;https://img.shields.io/librariesio/github/mahozad/android-pie-chart&#41;)
[comment]: <> (![Code Size]&#40;https://img.shields.io/github/languages/code-size/mahozad/android-pie-chart&#41;)
[comment]: <> (![Repo Size]&#40;https://img.shields.io/github/repo-size/mahozad/android-pie-chart&#41;)
[comment]: <> (![SLOC]&#40;https://img.shields.io/tokei/lines/github/mahozad/android-pie-chart&#41;)
[comment]: <> (![Downloads]&#40;https://img.shields.io/github/downloads/mahozad/android-pie-chart/total&#41;)
[comment]: <> (![Closed Issues]&#40;https://img.shields.io/github/issues-closed/mahozad/android-pie-chart?color=green&#41;)
[comment]: <> (![Commits Since Last Release]&#40;https://img.shields.io/github/commits-since/mahozad/android-pie-chart/latest&#41;)
[comment]: <> (![Release workflow]&#40;https://img.shields.io/github/workflow/status/mahozad/android-pie-chart/Test?label=CI%2FCD&#41;)
[comment]: <> (![Tests]&#40;https://img.shields.io/github/checks-status/mahozad/android-pie-chart/master&#41;)
[comment]: <> (![Milestone Progress]&#40;https://img.shields.io/github/milestones/progress-percent/mahozad/android-pie-chart/1&#41;)
[comment]: <> (![Lines of code]&#40;https://img.shields.io/tokei/lines/github/mahozad/android-pie-chart?color=%23efefef&#41;)
[comment]: <> ([![Latest release]&#40;https://img.shields.io/github/v/release/mahozad/android-pie-chart&#41;]&#40;https://github.com/mahozad/android-pie-chart/releases/latest&#41;)

[comment]: <> (!*†‡;)

[![Release workflow]](https://github.com/mahozad/android-pie-chart/actions/workflows/test.yml)
[![Codecov]](https://codecov.io/gh/mahozad/android-pie-chart)
[![Latest Maven Central release]](https://search.maven.org/artifact/ir.mahozad.android/pie-chart)
![kotlin]

![Logo]

A Pie/Donut<sup id="ref-1">[*]</sup>/Ring chart for Android, customizable to the most extent possible.  
For tutorial and examples refer to the [website].

### build.gradle[.kts]
```groovy
implementation("ir.mahozad.android:pie-chart:0.7.0")
```

### XML layout
```xml
<ir.mahozad.android.PieChart
    android:id="@+id/pieChart"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### Kotlin Activity
```kotlin
val pieChart = findViewById<PieChart>(R.id.pieChart)
pieChart.slices = listOf(
    PieChart.Slice(0.2f, Color.BLUE),
    PieChart.Slice(0.4f, Color.MAGENTA),
    PieChart.Slice(0.3f, Color.YELLOW),
    PieChart.Slice(0.1f, Color.CYAN)
)
```

### Jetpack Compose
```kotlin
@Composable
fun PieChartView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                slices = listOf(
                    PieChart.Slice(0.2f, Color.BLUE),
                    PieChart.Slice(0.4f, Color.MAGENTA),
                    PieChart.Slice(0.3f, Color.YELLOW),
                    PieChart.Slice(0.1f, Color.CYAN)
                )
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary
        }
    )
}
```

## Screenshots

<div align="center">

| Screenshot 1 | Screenshot 2 | Screenshot 3 | Screenshot 4 | Screenshot 5 |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| ![Screenshot 2] | ![Screenshot 1] | ![Screenshot 3] | ![Screenshot 4] | ![Screenshot 5] |

</div>

## Contributing

Please help improve the library by fixing [the issues that I couldn't tackle myself].  
Any other contributions are also welcome.

<br>

<sub><b id="footnote-1">*</b> Or *Doughnut* [↵]</sub>

  [*]: #footnote-1
  [↵]: #ref-1
  [Logo]: logo-animated.svg
  [kotlin]: https://img.shields.io/badge/kotlin-1.5.30-555.svg?labelColor=555&logo=data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAxOC45MyAxOC45MiIgd2lkdGg9IjE4IiBoZWlnaHQ9IjE4IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgogIDxyYWRpYWxHcmFkaWVudCBpZD0iZ3JhZGllbnQiIHI9IjIxLjY3OSIgY3g9IjIyLjQzMiIgY3k9IjMuNDkzIiBncmFkaWVudFRyYW5zZm9ybT0ibWF0cml4KDEgMCAwIDEgLTQuMTMgLTIuNzE4KSIgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiPgogICAgPHN0b3Agc3RvcC1jb2xvcj0iI2U0NDg1NyIgb2Zmc2V0PSIuMDAzIi8+CiAgICA8c3RvcCBzdG9wLWNvbG9yPSIjYzcxMWUxIiBvZmZzZXQ9Ii40NjkiLz4KICAgIDxzdG9wIHN0b3AtY29sb3I9IiM3ZjUyZmYiIG9mZnNldD0iMSIvPgogIDwvcmFkaWFsR3JhZGllbnQ+CiAgPHBhdGggZmlsbD0idXJsKCNncmFkaWVudCkiIGQ9Ik0gMTguOTMsMTguOTIgSCAwIFYgMCBIIDE4LjkzIEwgOS4yNyw5LjMyIFoiLz4KPC9zdmc+Cg==
  [website]: https://mahozad.ir/android-pie-chart/#examples
  [Codecov]: https://codecov.io/gh/mahozad/android-pie-chart/branch/master/graph/badge.svg?token=ptnbmXaozw
  [Release workflow]: https://github.com/mahozad/android-pie-chart/actions/workflows/ci.yml/badge.svg
  [Latest Maven Central release]: https://img.shields.io/maven-central/v/ir.mahozad.android/pie-chart?logo=android
  [the issues that I couldn't tackle myself]: https://github.com/mahozad/android-pie-chart/issues?q=is%3Aissue+is%3Aopen+label%3Acontribution-needed
  [Screenshot 1]: screenshots/screenshot-1.png
  [Screenshot 2]: screenshots/screenshot-2.png
  [Screenshot 3]: screenshots/screenshot-3.png
  [Screenshot 4]: screenshots/screenshot-4.png
  [Screenshot 5]: screenshots/screenshot-5.png
