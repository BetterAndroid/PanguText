# Quick Start

> Integrate `PanguText` into your project.

## Project Requirements

The project needs to be created using `Android Studio` or `IntelliJ IDEA` and be of type Android or Kotlin Multiplatform
project with integrated Kotlin environment dependencies.

- Android Studio (It is recommended to get the latest version [from here](https://developer.android.com/studio))

- IntelliJ IDEA (It is recommended to get the latest version [from here](https://www.jetbrains.com/idea))

- Kotlin 1.9.0+, Gradle 8+, Java 17+, Android Gradle Plugin 8+

### Configure Repositories

The dependencies of `PanguText` are published in **Maven Central** and our public repository.
You can use the following method to configure repositories.

We recommend using Kotlin DSL as the Gradle build script language and [SweetDependency](https://github.com/HighCapable/SweetDependency)
to manage dependencies.

#### SweetDependency (Recommended)

Configure repositories in your project's `SweetDependency` configuration file.

```yaml
repositories:
  google:
  maven-central:
  # (Optional) You can add this URL to use our public repository
  # When Sonatype-OSS fails and cannot publish dependencies, this repository is added as a backup
  # For details, please visit: https://github.com/HighCapable/maven-repository
  highcapable-maven-releases:
    url: https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases
```

#### Traditional Method

Configure repositories in your project `build.gradle.kts`.

```kotlin
repositories {
    google()
    mavenCentral()
    // (Optional) You can add this URL to use our public repository
    // When Sonatype OSS fails and cannot publish dependencies, this repository is added as a backup
    // For details, please visit: https://github.com/HighCapable/maven-repository
    maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
}
```

### Configure Java Version

Modify the Java version of Kotlin in your project `build.gradle.kts` to 17 or above.

> Kotlin DSL

```kt
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

## Functional Overview

The project is divided into multiple modules: Android platform and Jetpack Compose (multiplatform). You can choose the module you wish to include as a dependency in your project.

Click the corresponding module below to view detailed feature descriptions.

- [Android](../library/android.md)
- [Jetpack Compose](../library/compose.md)

## Demo

You can find some examples below. Check out the corresponding demo projects to get a better understanding of how these features work and quickly select the functionality you need.

- [Android](repo://tree/main/demo-android)
- [Jetpack Compose (Coming soon)](repo://tree/main/demo-compose)