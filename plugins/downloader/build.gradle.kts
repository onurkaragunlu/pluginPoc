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
        create("downloader") {
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
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
