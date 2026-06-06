enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
    }
}

plugins {
    id("com.highcapable.gropify") version "1.0.1"
}

gropify {
    global {
        android {
            includeKeys("^project\\..*$".toRegex())
            className = rootProject.name
            isRestrictedAccessEnabled = true
        }
        jvm {
            includeKeys("^project\\..*$".toRegex())
            className = rootProject.name
            isRestrictedAccessEnabled = true
        }
    }

    rootProject {
        common {
            isEnabled = false
        }
    }

    projects(":samples:demo-android") {
        android {
            isEnabled = false
        }
    }
}

rootProject.name = "PanguText"

include(":samples:demo-android")
include(":pangutext-android")