package com.karagunlu.plugins

import com.android.build.gradle.BaseExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.named
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File


/**
 * Created by Onur Karagünlü on 23.12.2024.
 */
class DownloaderPluginTest {

    private lateinit var project: Project
    private val mockObjectFactory: ObjectFactory = mockk(relaxed = true)
    private val mockDownloadHandler: DefaultDownloadHandler = mockk(relaxed = true)
    private val baseUrl = "www.example.com"
    private val sourceFolder = "/src/debug/file"

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.android.library")
        project.pluginManager.apply("org.jetbrains.kotlin.android")

        val androidExtension = project.extensions.getByType(BaseExtension::class.java)
        androidExtension.compileSdkVersion(35)
        androidExtension.namespace = "com.karagunlu.pluginpoc"

        applyDownloadPlugin()
    }

    // Fail expected
    @Test
    fun `copyFileToSrc task must depends on unzipFile`() {
        project.getTasksByName("tasks", false)
        val copyTask = project.tasks.getByName(TASK_COPY_TO_SRC).dependsOn.first() as Named

        copyTask.name shouldBe TASK_UNZIP
    }


    @Test
    fun `download task must depends on validate task`() {
        project.getTasksByName("tasks", false)

        val downloadTask = project.tasks.getByName(TASK_DOWNLOAD).dependsOn.first() as Named
        downloadTask.name shouldBe TASK_VALIDATE_URL
    }

    // Fail expected
    @Test
    fun `When download task is run,validate task should be run as well`() {
        project.getTasksByName("tasks", false)
        val downloadTask = project.tasks.named(TASK_DOWNLOAD).get()

        // Manually executing the task actions does not trigger dependencies (like TASK_VALIDATE_URL)
        downloadTask.actions.forEach { action ->
            action.execute(downloadTask)
        }

        verify {
            mockDownloadHandler.validateUrl(baseUrl)
        }
    }

    @Test
    fun `validateUrl method must be called with the parameter in the extension`() {
        project.getTasksByName("tasks", false)
        val downloadTask = project.tasks.named(TASK_VALIDATE_URL).get()

        downloadTask.actions.forEach { action ->
            action.execute(downloadTask)
        }

        verify {
            mockDownloadHandler.validateUrl(baseUrl)
        }
    }


    @Test
    fun `startDownload method must be called with the parameter in the extension`() {
        project.getTasksByName("tasks", false)
        val downloadTask = project.tasks.named(TASK_DOWNLOAD).get()

        downloadTask.actions.forEach { action ->
            action.execute(downloadTask)
        }

        verify {
            mockDownloadHandler.startDownload(baseUrl)
        }
    }

    @Test
    fun `copy task should copy the file to debug src`() {
        project.getTasksByName("tasks", false)

        //with out this
        //No copy destination directory has been specified, use 'into' to specify a target directory.
        every {
            mockDownloadHandler.copyFile(any<Copy>(), any<File>(), any<Directory>())
        } answers {
            val task = firstArg<Copy>()
            val input = secondArg<File>()
            val output = lastArg<Directory>()
            task.from(input)
            task.into(output)
        }

        val downloadTask = project.tasks.named(TASK_COPY_TO_SRC).get()

        val pathSlot = slot<Directory>()

        downloadTask.actions.forEach { action ->
            action.execute(downloadTask)
        }
        verify {
            mockDownloadHandler.copyFile(any(), any(), capture(pathSlot))
        }

        project.layout.projectDirectory.asFile.path + sourceFolder shouldBe pathSlot.captured.toString()

    }

    private fun applyDownloadPlugin() {
        every { mockObjectFactory.newInstance(DefaultDownloadHandler::class.java) } returns mockDownloadHandler
        val plugin = DownloaderPlugin(objectFactory = mockObjectFactory)
        plugin.apply(project)
        val extension = project.extensions.getByName(EXTENSION_NAME) as DownloaderExtension
        extension.baseUrl.set(baseUrl)
        extension.startDownload.set(true)
    }
}