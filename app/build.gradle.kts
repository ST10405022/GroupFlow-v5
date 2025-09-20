plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.example.groupflow"
    compileSdk = 36
    viewBinding.isEnabled = true

    defaultConfig {
        applicationId = "com.example.groupflow"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Unit testing
    testImplementation(libs.junit) // JUnit 5
    testImplementation(libs.mockk) // MockK
    testImplementation(libs.coroutines.test) // Coroutines test utilities
    testImplementation(libs.truth) // Truth assertions
    androidTestImplementation(libs.androidx.espresso.intents)

    //
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(platform(libs.firebase.bom)) // Firebase BOM
    implementation(libs.firebase.database) // Realtime Database
    implementation(libs.firebase.core) // Firebase Core
    implementation(libs.firebase.auth) // Firebase Auth
    implementation(libs.bumptech.glide) // Glide
    implementation(libs.firebase.messaging) // FCM
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.storage)

    implementation(libs.google.gson) // Gson
    implementation(libs.firebase.crashlytics) // Crashlytics
    testImplementation(libs.mockito.core) // Mockito for Java mocking
    testImplementation(libs.mockito.kotlin) // Mockito for Kotlin (extension helpers like mock(), whenever(), etc.)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ------------------------------
// Detekt configuration
// ------------------------------
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // Use your rules file
    config.from(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    ignoreFailures = true

    // Configure reports
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt/detekt.html"))
        xml.required.set(true)
        xml.outputLocation.set(file("build/reports/detekt/detekt.xml"))
        txt.required.set(false)
    }
}

ktlint {
    debug.set(true)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
