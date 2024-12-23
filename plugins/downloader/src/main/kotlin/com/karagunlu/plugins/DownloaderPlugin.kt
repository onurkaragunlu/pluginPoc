package com.karagunlu.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class DownloaderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.downloadPluginExtension()

        project.tasks.register("download") {
            doLast {
                if (!extension.startDownload.get()) {
                    println("Starting download from: ${extension.baseUrl.get()}")
                    extension.download()
                } else {
                    println("Download already started!")
                }
            }
        }
    }

    private fun Project.downloadPluginExtension() =
        extensions.create<DownloaderExtension>("downloader")
} 