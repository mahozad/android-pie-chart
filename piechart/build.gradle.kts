// Could also have used ${rootProject.extra["kotlinVersion"]}
val kotlinVersion: String by rootProject.extra

plugins {
    id("com.android.library")
    id("kotlin-android")
    // To also publish the module as a library in a maven repository to be globally available for everyone
    // To publish the library in Github packages see https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry
    // Could also publish to maven local repository with the task publishToMavenLocal
    id("maven-publish")
    // To generate a signature file for each artifact. In addition, checksum files will be generated for all artifacts and signature files.
    id("signing")
}

group = "io.github.mahozad"
version = "0.1.0"

android {
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("debug").java.srcDirs("src/debug/kotlin")
        getByName("release").java.srcDirs("src/release/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
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
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("runnerBuilder", "de.mannodermaus.junit5.AndroidJUnit5Builder")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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

// java {
//     withJavadocJar()
//     withSourcesJar()
// }
//
// // See https://github.com/kittinunf/fuel/blob/master/build.gradle.kts for example publishing block
// publishing {
//     publications {
//         create<MavenPublication>("mavenJava") {
//             artifactId = "my-library"
//             from(components["java"])
//             versionMapping {
//                 usage("java-api") {
//                     fromResolutionOf("runtimeClasspath")
//                 }
//                 usage("java-runtime") {
//                     fromResolutionResult()
//                 }
//             }
//             pom {
//                 name.set("My Library")
//                 description.set("A concise description of my library")
//                 url.set("http://www.example.com/library")
//                 properties.set(mapOf(
//                     "myProp" to "value",
//                     "prop.with.dots" to "anotherValue"
//                 ))
//                 licenses {
//                     license {
//                         name.set("The Apache License, Version 2.0")
//                         url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                     }
//                 }
//                 developers {
//                     developer {
//                         id.set("johnd")
//                         name.set("John Doe")
//                         email.set("john.doe@example.com")
//                     }
//                 }
//                 scm {
//                     connection.set("scm:git:git://example.com/my-library.git")
//                     developerConnection.set("scm:git:ssh://example.com/my-library.git")
//                     url.set("http://example.com/my-library/")
//                 }
//             }
//         }
//     }
//     repositories {
//         maven {
//             // change URLs to point to your repos, e.g. http://my.org/repo
//             val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
//             val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
//             url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//         }
//     }
// }
//
// signing {
//     sign(publishing.publications["mavenJava"])
// }
//
// tasks.withType(Javadoc::class) {
//     if (JavaVersion.current().isJava9Compatible) {
//         (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
//     }
// }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    debugImplementation("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.2.2")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.2.2")
}
