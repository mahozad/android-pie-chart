import com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD

// Could also have used ${rootProject.extra["kotlinVersion"]}
val kotlinVersion: String by rootProject.extra
val jacocoVersion: String by rootProject.extra

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka") version "1.4.32"
    id("jacoco")
    id("maven-publish")
    // To generate signature and checksum files for each artifact
    id("signing")
    // To print beautiful logs on the console while running tests with Gradle
    // Doesn't work for Android instrumented tests
    id("com.adarshr.test-logger") version "3.0.0"
}

group = "ir.mahozad.android"
version = "0.5.0"
val githubProjectName = "android-pie-chart"

android {
    sourceSets {
        get("main").java.srcDirs("src/main/kotlin")
        get("debug").java.srcDirs("src/debug/kotlin")
        get("release").java.srcDirs("src/release/kotlin")
        get("test").java.srcDirs("src/test/kotlin")
        get("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    // Since we are saving our screenshot tests on an external storage, we need to make sure that
    // we have *WRITE_EXTERNAL_STORAGE* permission added in the manifest.
    // When running on Marshmallow+, we also need to have those permissions granted before running a test.
    // -g is for granting permissions when installing the app (works on Marshmallow+ only) while -r is to allow reinstalling of the app.
    // These correspond to `adb shell pm install` options.
    // Just be aware that this does not work with Android Studio yet.
    //
    // See https://medium.com/stepstone-tech/how-to-capture-screenshots-for-failed-ui-tests-9927eea6e1e4
    adbOptions {
        installOptions("-g", "-r")
    }

    packagingOptions {
        exclude("META-INF/LICENSE*")
        exclude("META-INF/*.kotlin_module")
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }

    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 5
        versionName = project.version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("runnerBuilder", "de.mannodermaus.junit5.AndroidJUnit5Builder")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {
            isTestCoverageEnabled = true
            isMinifyEnabled = false
            isUseProguard = false
        }
        getByName("release") {
            isTestCoverageEnabled = true
            // There is no need to obfuscate an open source library
            // nor is it necessary to shrink the code because the user can do it
            isMinifyEnabled = false
            /**
             * NOTE: To remove Log statements in the application release,
             *  use the *proguard-android-optimize.txt* version.
             *  See [here](https://stackoverflow.com/q/33067142) and
             *      [here](https://stackoverflow.com/q/2446248/) for more information
             */
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

jacoco {
    toolVersion = jacocoVersion
}

apply(from = "${rootProject.projectDir}/scripts/configure-jacoco.gradle.kts")

/*
 * Configure the [test-logger plugin](https://github.com/radarsh/gradle-test-logger-plugin).
 * Also see [this](https://stackoverflow.com/q/3963708/)
 * and [this](https://stackoverflow.com/a/31774254/)
 */
testlogger {
    theme = STANDARD
    slowThreshold = 5000 /* ms */
    showSimpleNames = true
}

tasks.withType(Test::class) {
    // Specifies whether failing tests should fail the build
    ignoreFailures = false

    useJUnitPlatform {
        excludeEngines("junit-vintage")
    }
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        // https://github.com/gradle/gradle/issues/5184#issuecomment-457865951
        excludes = listOf("jdk.internal.*")
    }
}

/**
 * Uploading to a Maven repository requires sources and javadoc files as well.
 *
 * See [this gist](https://gist.github.com/kibotu/994c9cc65fe623b76b76fedfac74b34b) for groovy version.
 */
lateinit var sourcesArtifact: PublishArtifact
lateinit var javadocArtifact: PublishArtifact
tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
    }

    val dokkaHtml by getting(org.jetbrains.dokka.gradle.DokkaTask::class)

    val javadocJar by creating(Jar::class) {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }

    artifacts {
        sourcesArtifact = archives(sourcesJar)
        javadocArtifact = archives(javadocJar)
    }
}

/**
 * Maven Publish plugin allows you to publish build artifacts to an Apache Maven repository.
 * See [here](https://docs.gradle.org/current/userguide/publishing_maven.html)
 * and [here](https://developer.android.com/studio/build/maven-publish-plugin)
 * and [here](https://maven.apache.org/repository/guide-central-repository-upload.html)
 *
 * Use *publish* task to publish the artifact to the defined repositories.
 */
afterEvaluate {
    publishing {
        repositories {
            /* The Sonatype Maven Central repository is defined in the publish.gradle script */

            // GitHub Packages repository
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/mahozad/$githubProjectName")
                credentials {
                    username = project.properties["github.username"] as String? ?: System.getenv("GITHUB_ACTOR") ?: ""
                    password = project.properties["github.token"] as String? ?: System.getenv("GITHUB_TOKEN") ?: ""
                }
            }

            // Local repository which can be published to first to check artifacts
            maven {
                name = "LocalTestRepo"
                url = uri("file://${buildDir}/local-repository")
            }
        }
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("Release") {
                // Applies the component for the release build variant (two artifacts: the aar and the sources)
                from(components["release"])
                // You can then customize attributes of the publication as shown below
                groupId = "ir.mahozad.android"
                artifactId = "pie-chart"
                version = project.version.toString()
                artifact(sourcesArtifact)
                artifact(javadocArtifact)
                pom {
                    url.set("https://mahozad.ir/$githubProjectName")
                    name.set(githubProjectName)
                    description.set(
                        """
                        A library for creating pie charts and donut charts in Android.
                        The aim of this library is to provide a full-featured chart view and to enable users to customize it to the most extent possible.
                        Visit the project on GitHub to learn more.
                        """.trimIndent()
                    )
                    inceptionYear.set("2021")
                    licenses {
                        license {
                            name.set("Apache-2.0 License")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("mahozad")
                            name.set("Mahdi Hosseinzadeh")
                            url.set("https://mahozad.ir/")
                            email.set("")
                            roles.set(listOf("Lead Developer"))
                            timezone.set("GMT+4:30")
                        }
                    }
                    contributors {
                        // contributor {}
                    }
                    scm {
                        tag.set("HEAD")
                        url.set("https://github.com/mahozad/$githubProjectName")
                        connection.set("scm:git:github.com/mahozad/$githubProjectName.git")
                        developerConnection.set("scm:git:ssh://github.com/mahozad/$githubProjectName.git")
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/mahozad/$githubProjectName/issues")
                    }
                    ciManagement {
                        system.set("GitHub Actions")
                        url.set("https://github.com/mahozad/$githubProjectName/actions")
                    }
                }
            }
            create<MavenPublication>("Debug") {
                from(components["debug"])
                groupId = "ir.mahozad.android"
                artifactId = "pie-chart-debug"
                version = project.version.toString()
            }
        }
    }
}

// Usage: gradlew incrementVersion [-P[mode=major|minor|patch]|[overrideVersion=x]]
tasks.create("incrementVersion") {
    group = "versioning"
    description = "Increments the library version everywhere it is used."
    doLast {
        val (oldMajor, oldMinor, oldPatch) = version.toString().split(".")
        var (newMajor, newMinor, newPatch) = arrayOf(oldMajor, oldMinor, "0")
        when (properties["mode"]) {
            "major" -> newMajor = (oldMajor.toInt() + 1).toString().also { newMinor = "0" }
            "minor" -> newMinor = (oldMinor.toInt() + 1).toString()
            else    -> newPatch = (oldPatch.toInt() + 1).toString()
        }
        var newVersion = "$newMajor.$newMinor.$newPatch"
        val newVersionCode = (android.defaultConfig.versionCode ?: 0) + 1
        properties["overrideVersion"]?.toString()?.let { newVersion = it }
        with(file("../README.md")) {
            writeText(
                readText()
                .replaceFirst(":$version", ":$newVersion"))
        }
        with(buildFile) {
            writeText(
                readText()
                .replaceFirst(Regex("\"$version\""), "\"$newVersion\"")
                .replaceFirst(Regex("versionCode = \\d+"), "versionCode = $newVersionCode")
            )
        }
    }
}

// val PUBLISH_GROUP_ID by extra("ir.mahozad.android")
// val PUBLISH_ARTIFACT_ID by extra("pie-chart")
// val PUBLISH_VERSION by extra("0.1.0")

apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")

dependencies {
    /**
     * NOTE: Could not add *androidx:appcompat* library for androidTest configuration
     *    because it resulted in conflicting versions of androidx:lifecycle dependency
     *    that could not be resolved (by, for example, forcing a specific version of it).
     */
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("org.assertj:assertj-core:3.20.2")
    androidTestImplementation("androidx.constraintlayout:constraintlayout:2.1.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("org.assertj:assertj-core:3.20.2")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.2.2")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.2.2")
}
