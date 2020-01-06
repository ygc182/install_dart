package com.xxbmm.flutter_install.downloader

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.xxbmm.flutter_install.FlutterInstallPlugin
import java.io.File
import java.lang.Exception

class DownLoaderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if(DownLoaderManager.downloadList.size <= 0) return
            val query = DownloadManager.Query()
            for (bean in DownLoaderManager.downloadList) {
                query.setFilterById(bean.downloadId)
                val cursor = DownLoaderManager.downloadManager?.query(query)
                if(cursor != null && cursor.moveToFirst()){
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when(status){
                        DownloadManager.STATUS_PAUSED -> Toast.makeText(context, bean.title + "下载暂停", Toast.LENGTH_SHORT).show()
                        DownloadManager.STATUS_PENDING -> {
                        }
                        DownloadManager.STATUS_RUNNING -> Toast.makeText(context, bean.title + "正在下载", Toast.LENGTH_SHORT).show()
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Toast.makeText(context, bean.title + "下载成功", Toast.LENGTH_SHORT).show()
                            cursor.close()
                            DownLoaderManager.downloadList.remove(bean)
                            if (context != null && bean.downloadType === DownloadBean.TYPE_APK && bean.needInstall) {
                                installApk(context, bean.downloadId, bean)
                            }
                        }
                        //下载失败
                        DownloadManager.STATUS_FAILED -> {
                            Toast.makeText(context, bean.title + "下载失败", Toast.LENGTH_SHORT).show()
                            DownLoaderManager.downloadList.remove(bean)
                            cursor.close()
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun installApk(activity: Context, downloadApkId: Long, downloadBean: DownloadBean) {
        val dManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val apkUri = dManager.getUriForDownloadedFile(downloadApkId)
        if(apkUri != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                installApkAboveN(activity, apkUri)
            } else {
                installApkBelowN(activity, apkUri)
            }
        } else {
            Toast.makeText(activity, downloadBean.title + "下载错误", Toast.LENGTH_SHORT).show()
        }

    }

    private fun installApkBelowN(context: Context, uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * android7.x (Build.VERSION_CODES.N)及以上安装需要通过 ContentProvider 获取文件Uri，
     * 需在应用中的AndroidManifest.xml 文件添加 provider 标签，
     * 并新增文件路径配置文件 res/xml/provider_path.xml
     */
    private fun installApkAboveN(context: Context,uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}