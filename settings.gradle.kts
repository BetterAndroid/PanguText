enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.5"
}
sweetProperty {
    rootProject { all { isEnable = false } }
    project(":pangutext-android") {
        sourcesCode {
            isEnableRestrictedAccess = true
        }
    }
}
rootProject.name = "PanguText"
include(":demo-android")
include(":pangutext-android")