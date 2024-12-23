package com.karagunlu.plugins

/**
 * Created by Onur Karagünlü on 23.12.2024.
 */
interface DownloadHandler {
    fun startDownload(url: String)
    fun validateUrl(url: String)
}

open class DefaultDownloadHandler : DownloadHandler {
    override fun startDownload(url: String) {
        println("Download started...")
    }

    override fun validateUrl(url: String) {
        println("Validating Url...")
    }
}