package com.xxbmm.flutter_install.downloader

class DownloadBean {
    companion object {
        const val TYPE_APK = "APK"
        const val TYPE_FILE = "FILE"
    }

    var downloadId = 0L
    var title: String = ""
    var downloadType : String = TYPE_APK
    var needInstall : Boolean = false
}