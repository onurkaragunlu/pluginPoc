package com.karagunlu.plugins

import com.android.build.gradle.BaseExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * Created by Onur Karagünlü on 23.12.2024.
 */
class DownloaderPluginTest {

    private lateinit var project: Project
    private val mockObjectFactory: ObjectFactory = mockk(relaxed = true)
    private val mockDownloadHandler: DefaultDownloadHandler = mockk(relaxed = true)
    private val baseUrl = "www.example.com"

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.plugins.apply("com.android.library")
        project.plugins.apply("org.jetbrains.kotlin.android")
        val androidExtension = project.extensions.getByType(BaseExtension::class.java)
        androidExtension.compileSdkVersion(35)
        androidExtension.namespace = "com.karagunlu.pluginpoc"
        applyDownloadPlugin()
    }

    @Test
    fun `copyFileToSrc task should depends on unzipFile`() {
        project.getTasksByName("tasks", false)
        val taskName = project.tasks.getByName("copyFileToSrc").dependsOn.first() as Named
        taskName.name shouldBe "copyFileToSrc"
    }


    private fun applyDownloadPlugin() {
        every { mockObjectFactory.newInstance(DefaultDownloadHandler::class.java) } returns mockDownloadHandler
        val plugin = DownloaderPlugin(objectFactory = mockObjectFactory)
        plugin.apply(project)
        val extension = project.extensions.getByName("downloader") as DownloaderExtension
        extension.baseUrl.set(baseUrl)
        extension.startDownload.set(true)
    }
}