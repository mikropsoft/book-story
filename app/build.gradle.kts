plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")

    id("com.google.dagger.hilt.android")
    kotlin("kapt")

    id("kotlin-parcelize")
}

android {
    namespace = "com.acclorite.books_history"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.acclorite.books_history"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }


//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            signingConfig = signingConfigs.getByName("debug")
//        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    val composeVersion = "1.5.7"

    // Default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.ui:ui-android:1.6.0")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material:1.6.0")

    // Unit tests
    testImplementation("androidx.test:core:latest.release")
    testImplementation("junit:junit:latest.release")
    testImplementation("androidx.arch.core:core-testing:latest.release")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:latest.release")
    testImplementation("com.google.truth:truth:1.2.0")
    testImplementation("androidx.test.ext:truth:1.5.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:latest.release")
    testImplementation("io.mockk:mockk:latest.release")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

    // Instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:latest.release")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:latest.release")
    androidTestImplementation("junit:junit:latest.release")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:latest.release")
    androidTestImplementation("androidx.arch.core:core-testing:latest.release")
    androidTestImplementation("com.google.truth:truth:latest.release")
    androidTestImplementation("androidx.test.ext:junit:latest.release")
    androidTestImplementation("androidx.test:core-ktx:latest.release")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:latest.release")
    androidTestImplementation("io.mockk:mockk-android:latest.release")
    androidTestImplementation("androidx.test:runner:latest.release")

    // All dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.2-alpha")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("com.google.dagger:hilt-compiler:latest.release")
    kapt("androidx.hilt:hilt-compiler:latest.release")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:latest.release")

    // Splash API
    implementation("androidx.core:core-splashscreen:latest.release")

    // Permission Handling
    implementation("com.google.accompanist:accompanist-permissions:latest.release")

    // Pdf parser
    implementation("com.tom-roush:pdfbox-android:latest.release")

    // Epub parser
    implementation("nl.siegmann.epublib:epublib-core:latest.release") {
        exclude("org.slf4j")
        exclude("xmlpull")
    }
    implementation("org.slf4j:slf4j-android:latest.release")
    implementation("org.jsoup:jsoup:latest.release")
}