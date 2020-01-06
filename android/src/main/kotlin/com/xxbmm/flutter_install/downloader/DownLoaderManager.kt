package com.xxbmm.flutter_install.downloader

import android.app.DownloadManager
import android.content.Context

class DownLoaderManager {
    companion object {

        var downloadList: MutableList<DownloadBean> = mutableListOf()
        var downloadManager: DownloadManager? = null
        fun init(context: Context){
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
    }
}