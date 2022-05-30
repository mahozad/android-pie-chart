# Notes
- Make sure the device screen is on and unlocked for the activity to go to resumed state.
- For a complete and detailed answer about how to set up screenshot tests see
  [this post](https://stackoverflow.com/a/69259011) for *View* and
  [this post](https://stackoverflow.com/a/69176420) for *Compose*.
- If the repository size gets big due to the history of screenshot files,
  we can delete older versions of the screenshots from VCS.
  See [this post](https://stackoverflow.com/q/26831494/).
- Because JUnit 5 is built on Java 8 from the ground up, its instrumentation tests
  will only run on devices running Android 8.0 (API 26) or newer. Older phones will
  skip the execution of these tests completely, marking them as "ignored".
- Do not change the device display dimensions in
  *Settings* -> *Developer options* -> *Smallest width* OR in *Settings* -> *Display* -> *Display size*
  as it impacts the screenshots and other measurement tests.
- The screenshot tests do not work the same on the GitHub emulator
  (even though the emulator device defined in ci.yml is the same as our main local device).
  Probably, it is because of the previous note (modified display dimensions on our local device).
  So, we had to disable them on GitHub. Read on to see how.
- Because the tests are instrumented tests and run in the JVM of the device,
  they cannot see the environment variables of the OS the device and the tests run in.
  So, annotations like [DisabledOnOs] or [DisabledIfEnvironmentVariable] do not
  have access to the OS environment variables and hence do not work as intended.
  The [Disabled] annotation works correctly, however.

  See [this](https://stackoverflow.com/q/42675547) and [this](https://stackoverflow.com/q/40156906) Stack Overflow posts.

- We can use the [update-screenshots script in *scripts/* directory](../../../scripts/update-test-screenshots.main.kts)
  to update the screenshots. We could also have implemented a custom `ScreenCaptureProcessor`
  in the `takeScreenshot` function to name the screenshots without the UUID; but then,
  we could not copy the new screenshots to the *assets/* directory to compare with the old ones.
- See [this article](https://medium.com/stepstone-tech/exploring-androidjunitrunner-filtering-options-df26d30b4f60)
  about Android instrumentation runner arguments and how to use them.

  To pass instrumentation arguments when running with Gradle see
  [this post](https://stackoverflow.com/a/46183452).

  To pass instrumentation arguments for an IDEA *Android Instrumentation Test* configuration,
  click the *...* button in front of *Instrumentation arguments:* section.

- We used a custom `BuildConfig` field to check if we are running on CI.
  See the build script -> `android` -> `buildTypes` -> `debug`.

  Another solution would be to use `InstrumentationRegistry.getArguments()`.
  See [this post](https://stackoverflow.com/a/46183452).

  Another solution would be the following.
  See [this](https://stackoverflow.com/q/42675547) and [this](https://stackoverflow.com/q/40156906) SO posts.
  ```kotlin
  buildTypes {
      create("local") {
          initWith(buildTypes["debug"])
          buildConfigField("Boolean", "CI", "${System.getenv("CI") == "true"}")
          isDebuggable = true
      }
      testBuildType = "local"
  }
  ```
  With this solution, do not forget to change the build variant to "local" in the IDE.

  - We are using JUnit 4 tests for Compose tests because of the need for [createComposeRule]. 
    These tests can be run along with other JUnit 5 test classes with no problem. 
  - By default `composeTestRule.mainClock.autoAdvance` is `true` so the
    screenshots are captured when the animations are finished and the compose is idle.

## Screenshot tests
These tests are used to visually inspect the chart to avoid any regressions.
Also, they are used to test whether changing chart properties work as expected.
They are a kind of end-to-end testing.

## New features in *androidx.test.core-ktx version 1.4.1*

Add new experimental APIs for screenshots:
  - `View.captureToBitmap` extension function
  - `Window.captureRegionToBitmap` extension function
  - `takeScreenshot()`
  - Add experimental `Bitmap.writeToTestStorage` API

## New features in *Compose UI 1.2.0-beta01*:

Introduced new experimental, platform independent, test API: an interface *ComposeUiTest* and a
`fun runComposeUiTest(block: ComposeUiTest.() -> Unit)`, that can be used to run Compose Ui tests without
the need for a TestRule. To run a test without a *ComposeTestRule*, pass the test as a lambda to *runComposeUiTest*,
and use the methods and members in the receiver scope *ComposeUiTest*, which are the same ones as in *
ComposeContentTestRule*.

The Android specific interface *AndroidComposeUiTest*
and `fun runAndroidComposeUiTest(block: AndroidComposeUiTest.() -> Unit)`
are added to provide access to the underlying *Activity*, similar to *AndroidComposeTestRule*.
For even more control, you can instantiate a class *AndroidComposeUiTestEnvironment* yourself.

The Desktop implementation is the class *DesktopComposeUiTest*, but no Desktop specific run functions are offered at the
moment.

Migrating a test from a *ComposeTestRule* to *ComposeUiTest* can be done like this (Android example).
From:

```kotlin
@RunWith(AndroidJUnit4::class)
class MyTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun test() {
        rule.setContent {
            Text("Hello Compose!")
        }
        rule.onNodeWithText("Hello Compose!").assertExists()
    }
}
```

To:

```kotlin
@RunWith(AndroidJUnit4::class)
class MyTest {
    @Test
    @OptIn(ExperimentalTestApi::class)
    fun test() = runComposeUiTest {
        setContent {
            Text("Hello Compose!")
        }
        onNodeWithText("Hello Compose!").assertExists()
    }
}
```
