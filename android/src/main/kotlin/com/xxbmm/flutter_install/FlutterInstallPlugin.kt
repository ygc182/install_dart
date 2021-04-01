package com.xxbmm.flutter_install

import android.app.DownloadManager
import android.net.Uri
import android.widget.Toast
import com.xxbmm.flutter_install.downloader.DownLoaderManager
import com.xxbmm.flutter_install.downloader.DownloadBean
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class FlutterInstallPlugin : MethodCallHandler, FlutterPlugin {
    private var apkUri: Uri? = null
    private var binding: FlutterPlugin.FlutterPluginBinding? = null
    private var channel: MethodChannel? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.binding = binding
        channel = MethodChannel(binding.binaryMessenger, "install_plugin")
        channel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "downloadApk") {
            val url = call.argument<String>("url")

            if (url == null) {
                result.error("NullPointerException", "url is null", null)
                return
            }
            try {
                downloadApk("小小包麻麻", url)
            } catch (e: Throwable) {
                result.error(e.javaClass.simpleName, e.message, null)
            }

        } else {
            result.notImplemented()
        }
    }

    private fun downloadApk(title: String, url: String, needInstall: Boolean = true) {
        this.binding?.apply {
            DownLoaderManager.init(applicationContext)
            var downloadId = 0L
            applicationContext.run {
                Toast.makeText(this, "正在下载$title", Toast.LENGTH_SHORT).show()
                // 真正的下载逻辑
                val request = DownloadManager.Request(Uri.parse(url))
                request.setTitle(title)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setMimeType("application/vnd.android.package-archive")
                request.setDescription("正在下载...")

                try {
                    if (DownLoaderManager.downloadManager == null) {
                        // 可能部分机型不支持这个DownloaderManger ，这边可以使用自定义的下载框架
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                request.setDestinationUri(apkUri)
                if (DownLoaderManager.downloadManager != null) {
                    downloadId = DownLoaderManager.downloadManager!!.enqueue(request)
                    val downloadBean = DownloadBean()
                    downloadBean.downloadId = downloadId
                    downloadBean.title = title
                    downloadBean.downloadType = DownloadBean.TYPE_APK
                    downloadBean.needInstall = needInstall
                    DownLoaderManager.downloadList.add(downloadBean)
                }
            }
        }
    }

}
