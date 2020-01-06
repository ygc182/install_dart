package com.xxbmm.flutter_install

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.xxbmm.flutter_install.downloader.DownLoaderManager
import com.xxbmm.flutter_install.downloader.DownloadBean
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File

class FlutterInstallPlugin(private val mRegister: Registrar) : MethodCallHandler {
    private var apkUri: Uri? = null
    companion object {

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "install_plugin")
            val installPlugin = FlutterInstallPlugin(registrar)
            channel.setMethodCallHandler(installPlugin)
        }

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
        DownLoaderManager.init(mRegister.activeContext())
        var downloadId = 0L
        mRegister.activeContext().run {
            // 这边是初始化download错误的时候

//            var fileName = if (url.contains(".apk")) {
//                url.substring(url.lastIndexOf("/"), url.length)
//            } else {
//                "xxbmm_" + System.currentTimeMillis() + ".apk"
//            }
//            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            // 假如这个apk已经下载了，然后需要直接安装，那么就直接安装
//            if (file.exists() && needInstall && DownLoaderManager.downloadComplete) {
//                install(this, file.absolutePath)
//                return
//            }
            Toast.makeText(this,"正在下载$title", Toast.LENGTH_SHORT).show()
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

    private fun install(context: Context, filePath: String) {
        val apkFile = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }
}
