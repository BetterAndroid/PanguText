plugins {
    autowire(libs.plugins.android.library)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.kotlin.dokka)
    autowire(libs.plugins.maven.publish)
}

group = property.project.groupName
version = property.project.pangutext.android.version

android {
    namespace = property.project.pangutext.android.namespace
    compileSdk = property.project.android.compileSdk

    defaultConfig {
        minSdk = property.project.android.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    implementation(com.highcapable.betterandroid.ui.extension)
    implementation(com.highcapable.betterandroid.system.extension)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    testImplementation(junit.junit)
    androidTestImplementation(androidx.test.ext.junit)
    androidTestImplementation(androidx.test.espresso.espresso.core)
}