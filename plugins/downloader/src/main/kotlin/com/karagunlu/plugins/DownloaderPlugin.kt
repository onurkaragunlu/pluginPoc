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
const val TASK_DOWNLOAD = "downloadTask"
const val TASK_VALIDATE_URL = "validateUrl"
const val TASK_UNZIP = "unzipFile"
const val TASK_COPY_TO_SRC = "copyFileToSrc"
const val EXTENSION_NAME = "downloader"

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
        copyToSourceFolder(downloadHandler)
    }

    private fun Project.downloadPluginExtension() =
        extensions.create<DownloaderExtension>(EXTENSION_NAME)
}

fun Project.downloadFile(url: String, downloadHandler: DownloadHandler) {
    tasks.register(TASK_DOWNLOAD) {
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
    return tasks.register(TASK_VALIDATE_URL) {
        logger.lifecycle("Configuration Phase - validateUrlTask ")
        doLast {
            downloadHandler.validateUrl(url)
        }
    }
}

fun Project.copyToSourceFolder(downloadHandler: DownloadHandler) {
    val unzipTask = tasks.named<Copy>(TASK_UNZIP)
    val inputdir = unzipTask.map { it.destinationDir }
    val outputDir = layout.projectDirectory.dir("src/debug/file")

    tasks.register<Copy>(TASK_COPY_TO_SRC) {
        inputs.dir(inputdir)
        outputs.dir(outputDir)
        downloadHandler.copyFile(this@register, inputdir.get(), outputDir)
    }
}

fun Project.unzipFile() {
    tasks.register<Copy>(TASK_UNZIP) {
        createFakeFile()
    }.dependsOn(TASK_DOWNLOAD)
}

private fun Copy.createFakeFile() {
    val sourceDir = project.layout.buildDirectory.dir("zipfile").get().asFile
    val targetDir = project.layout.buildDirectory.dir("unzipfile").get().asFile
    val fileName = "example.txt"

    if (!sourceDir.exists()) {
        sourceDir.mkdirs()
    }

    val file = File(sourceDir, fileName)
    if (!file.exists()) {
        file.writeText("Hello, this is a generated file.")
        logger.lifecycle("File created at: ${file.absolutePath}")
    }
    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }
    inputs.dir(sourceDir)
    outputs.dir(targetDir)
    from(sourceDir)
    into(targetDir)
    doLast {
        println("Files copied.")
    }
}