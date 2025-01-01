package com.karagunlu.plugins

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import java.io.File

/**
 * Created by Onur Karagünlü on 23.12.2024.
 */
interface DownloadHandler {
    fun startDownload(url: String)
    fun validateUrl(url: String)
    fun copyFile(copy: Copy, input: Provider<File>, output: Directory)
}

open class DefaultDownloadHandler : DownloadHandler {
    override fun startDownload(url: String) {
        println("Download started...")
    }

    override fun validateUrl(url: String) {
        println("Validating Url...")
    }

    override fun copyFile(copy: Copy, input: Provider<File>, output: Directory) {
        println("Copy Files...")
        copy.from(input)
        copy.into(output)
    }
}