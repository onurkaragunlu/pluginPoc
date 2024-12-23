package com.karagunlu.plugins

import org.gradle.api.provider.Property

abstract class DownloaderExtension {
    /**
     * Base URL for downloading resources.
     */
    abstract val baseUrl: Property<String>

    /**
     * Indicates if the download process has started.
     * This is internal and should not be modified externally.
     */
    internal abstract val startDownload: Property<Boolean>

    /**
     * Triggers the download process by setting [startDownload] to true.
     */
    fun download() {
        startDownload.set(true)
    }
} 