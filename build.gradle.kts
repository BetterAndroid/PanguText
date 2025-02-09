import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    autowire(libs.plugins.android.application) apply false
    autowire(libs.plugins.android.library) apply false
    autowire(libs.plugins.kotlin.android) apply false
    autowire(libs.plugins.kotlin.dokka) apply false
    autowire(libs.plugins.maven.publish) apply false
}

libraryProjects {
    afterEvaluate {
        configure<PublishingExtension> {
            repositories {
                val repositoryDir = gradle.gradleUserHomeDir
                    .resolve("highcapable-maven-repository")
                    .resolve("repository")
                maven {
                    name = "HighCapableMavenReleases"
                    url = repositoryDir.resolve("releases").toURI()
                }
                maven {
                    name = "HighCapableMavenSnapShots"
                    url = repositoryDir.resolve("snapshots").toURI()
                }
            }
        }
        configure<MavenPublishBaseExtension> {
            configure(AndroidSingleVariantLibrary(publishJavadocJar = false))
        }
    }
    tasks.withType<DokkaTask>().configureEach {
        val configuration = """{ "footerMessage": "PanguText | Apache-2.0 License | Copyright (C) 2019 HighCapable" }"""
        pluginsMapConfiguration.set(mapOf("org.jetbrains.dokka.base.DokkaBase" to configuration))
    }
    tasks.register("publishKDoc") {
        group = "documentation"
        dependsOn("dokkaHtml")
        doLast {
            val docsDir = rootProject.projectDir
                .resolve("docs-source")
                .resolve("dist")
                .resolve("KDoc")
                .resolve(project.name)
            if (docsDir.exists()) docsDir.deleteRecursively() else docsDir.mkdirs()
            layout.buildDirectory.dir("dokka/html").get().asFile.copyRecursively(docsDir)
        }
    }
}

fun libraryProjects(action: Action<in Project>) {
    val libraries = listOf(
        Libraries.PANGUTEXT_ANDROID,
        Libraries.PANGUTEXT_COMPOSE
    )
    allprojects { if (libraries.contains(name)) action.execute(this) }
}

object Libraries {
    const val PANGUTEXT_ANDROID = "pangutext-android"
    const val PANGUTEXT_COMPOSE = "pangutext-compose"
}