plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")  // Compose compiler built-in for Kotlin 1.9.x
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")  // KAPT is built-in with kotlin-android plugin
    id("com.google.dagger.hilt")
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}