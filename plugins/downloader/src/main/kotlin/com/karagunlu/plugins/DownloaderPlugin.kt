package com.karagunlu.plugins

import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.register
import java.io.File
import javax.inject.Inject

private const val GROUP = "DownloadFile"

class DownloaderPlugin @Inject constructor(
    private val objectFactory: ObjectFactory,
) : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.downloadPluginExtension()
        val downloadHandler = objectFactory.newInstance<DefaultDownloadHandler>()

        project.afterEvaluate {
            if (extension.startDownload.orNull == true) {
                registerTask(extension, downloadHandler)
            }
        }
    }

    private fun Project.registerTask(
        extension: DownloaderExtension,
        downloadHandler: DefaultDownloadHandler
    ) {
        downloadFile(extension.baseUrl.get(), downloadHandler)
        unzipFile()
        copyToSourceFolder()
    }

    private fun Project.downloadPluginExtension() =
        extensions.create<DownloaderExtension>("downloader")
}

fun Project.downloadFile(url: String, downloadHandler: DownloadHandler) {
    tasks.register("downloadTask") {
        group = GROUP
        logger.lifecycle("Configuration Phase -downloadFile")
        doLast {
            downloadHandler.startDownload(url)
        }
    }.dependsOn(validateUrlTask(url, downloadHandler))

}

fun Project.validateUrlTask(
    url: String,
    downloadHandler: DownloadHandler
): TaskProvider<Task> {
    return tasks.register("validateUrl") {
        logger.lifecycle("Configuration Phase - validateUrlTask ")
        doLast {
            downloadHandler.validateUrl(url)
        }
    }
}

fun Project.copyToSourceFolder() {
    val inputdir = tasks.named<Copy>("unzipFile").flatMap { task ->
        provider { task.destinationDir }
    }
    logger.lifecycle("${inputdir.get()}")
    val outputDir = layout.projectDirectory.dir("src/debug/file")

    tasks.register<Copy>("copyFileToSrc") {
        inputs.dir(inputdir)
        outputs.dir(outputDir)
        from(inputdir.get())
        into(outputDir)
    }
}

fun Project.unzipFile() {
    tasks.register<Copy>("unzipFile") {
        createFakeFile()
    }.dependsOn("downloadTask")
}

private fun Copy.createFakeFile() {
    val sourceDir = project.layout.buildDirectory.dir("build/zipfile").get().asFile
    val targetDir = project.layout.buildDirectory.dir("build/unzipfile").get().asFile
    val fileName = "example.txt"

    // Create the source directory if it doesn't exist
    if (!sourceDir.exists()) {
        sourceDir.mkdirs()
    }

    // Create the file
    val file = File(sourceDir, fileName)
    if (!file.exists()) {
        file.writeText("Hello, this is a generated file.")
        logger.lifecycle("File created at: ${file.absolutePath}")
    }
    // Ensure the target directory exists
    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }
    from(sourceDir)
    into(targetDir)
}