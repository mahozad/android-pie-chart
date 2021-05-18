// Could also have used ${rootProject.extra["kotlinVersion"]}
val kotlinVersion: String by rootProject.extra

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    // To generate signature and checksum files for each artifact
    id("signing")
}

group = "ir.mahozad.android"
version = "0.1.0"

android {
    sourceSets {
        get("main").java.srcDirs("src/main/kotlin")
        get("debug").java.srcDirs("src/debug/kotlin")
        get("release").java.srcDirs("src/release/kotlin")
        get("test").java.srcDirs("src/test/kotlin")
        get("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    packagingOptions {
        exclude("META-INF/LICENSE*")
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
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("runnerBuilder", "de.mannodermaus.junit5.AndroidJUnit5Builder")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
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

tasks.withType(Test::class) {
    useJUnitPlatform {
        excludeEngines("junit-vintage")
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

    val javadoc by creating(Javadoc::class) {
        isFailOnError = false
        source = android.sourceSets["main"].java.getSourceFiles()
        classpath += project.files(android.bootClasspath.plus(File.pathSeparator))
        classpath += configurations.compile
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.destinationDir)
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
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("PieChartReleaseForMaven") {
                // Applies the component for the release build variant (two artifacts: the aar and the sources)
                from(components["release"])
                // You can then customize attributes of the publication as shown below
                groupId = "ir.mahozad.android"
                artifactId = "pie-chart"
                version = "0.1.0"
                artifact(sourcesArtifact)
                artifact(javadocArtifact)
                pom {
                    val githubProjectName = "android-pie-chart"
                    name.set(githubProjectName)
                    description.set("An Android library for creating pie charts and donut charts")
                    url.set("https://github.com/mahozad/$githubProjectName")
                    licenses {
                        license {
                            name.set("Apache-2.0 License")
                            url.set("https://github.com/mahozad/$githubProjectName/blob/main/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("mahozad")
                            name.set("Mahdi Hosseinzadeh")
                            email.set("")
                        }
                        // Other developers...
                    }
                    scm {
                        connection.set("scm:git:github.com/mahozad/$githubProjectName.git")
                        developerConnection.set("scm:git:ssh://github.com/mahozad/$githubProjectName.git")
                        url.set("https://github.com/mahozad/$githubProjectName/tree/main")
                    }
                }
            }
            create<MavenPublication>("PieChartDebugForMaven") {
                from(components["debug"])
                groupId = "ir.mahozad.android"
                artifactId = "pie-chart-debug"
                version = "0.1.0"
            }
        }
    }
}

// val PUBLISH_GROUP_ID by extra("ir.mahozad.android")
// val PUBLISH_ARTIFACT_ID by extra("pie-chart")
// val PUBLISH_VERSION by extra("0.1.0")

apply("${rootProject.projectDir}/scripts/publish-module.gradle")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    debugImplementation("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.2.2")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.2.2")
}
