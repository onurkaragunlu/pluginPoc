package com.karagunlu.plugins

import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.newInstance
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
                downloadFile(extension.baseUrl.get(), downloadHandler)
            }
        }
    }

    private fun Project.downloadPluginExtension() =
        extensions.create<DownloaderExtension>("downloader")
}

fun Project.downloadFile(url: String, downloadHandler: DownloadHandler) {
    tasks.register("DownloadTask") {
        group = GROUP
        logger.lifecycle("downloadFile -- registered")
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
        logger.lifecycle("validateUrlTask -- registered")
        doLast {
            downloadHandler.validateUrl(url)
        }
    }
}