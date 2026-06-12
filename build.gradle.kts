plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:1.9.23")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.23")
        classpath("com.google.devtools.kapt:com.google.devtools.kapt.gradle.plugin:1.9.23")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}