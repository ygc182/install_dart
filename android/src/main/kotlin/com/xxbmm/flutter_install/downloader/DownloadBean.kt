package com.xxbmm.flutter_install.downloader

class DownloadBean {
    companion object {
        const val TYPE_APK = "APK"
        const val TYPE_FILE = "FILE"
    }

    var downloadId = 0L
    var title: String = "小小包麻麻"
    var downloadType : String = TYPE_APK
    var needInstall : Boolean = false
}