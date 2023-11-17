package com.xxbmm.flutter_install.downloader

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter

class DownLoaderManager {
    companion object {

        var downloadList: MutableList<DownloadBean> = mutableListOf()
        var downloadManager: DownloadManager? = null
        fun init(context: Context){
            context.registerReceiver(DownLoaderBroadcastReceiver(), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
    }
}