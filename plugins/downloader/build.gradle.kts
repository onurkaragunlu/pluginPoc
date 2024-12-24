import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.karagunlu.plugins"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("downloaderPlugin") {
            id = "com.karagunlu.downloader"
            implementationClass = "com.karagunlu.plugins.DownloaderPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation(libs.android.gradle)

    // Test dependencies
    testImplementation(gradleApi())
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
    testImplementation(gradleTestKit())
    testImplementation(libs.kotlin.gradle)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_18)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
