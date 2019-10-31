package com.xxbmm.flutter_install

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileNotFoundException
import java.lang.NullPointerException

class FlutterInstallPlugin(private val mRegister: Registrar) : MethodCallHandler {
    private var apkFile: File? = null

    companion object {
        private const val TAG = "FlutterInstallPlugin"
        private const val installRequestCode = 101

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "install_plugin")
            val installPlugin = FlutterInstallPlugin(registrar)
            channel.setMethodCallHandler(installPlugin)
            registrar.addActivityResultListener { requestCode, resultCode, intent ->
                Log.i(TAG, "requestCode=$requestCode, resultCode = $resultCode, intent = $intent")
                if (resultCode == Activity.RESULT_OK && requestCode == installRequestCode) {
                    installPlugin.installApkAboveN(registrar.context(), installPlugin.apkFile)
                    true
                } else {
                    Log.e(TAG, "install failed: refused to install apk from unknown sources")
                    false
                }

            }
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "installApk") {
            val apkFilePath = call.argument<String>("filePath")
            apkFile = File(apkFilePath)

            if (apkFile == null) {
                result.error("NullPointerException", "filePath is null", null)
                return
            } else if (!apkFile!!.exists()) {
                result.error("NullPointerException", "apkFile is not exists", null)
                return
            }
            try {
                installApk(apkFile!!)
            } catch (e: Throwable) {
                result.error(e.javaClass.simpleName, e.message, null)
            }

        } else {
            result.notImplemented()
        }
    }

    private fun installApk(file: File) {
        val activity: Activity = mRegister.activity()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity.packageManager.canRequestPackageInstalls()) {
                installApkAboveN(mRegister.activity(), apkFile)
            } else {
                showSettingPackageInstall(activity)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installApkAboveN(mRegister.activity(), apkFile)
        } else {
            installApkBelowN(file)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSettingPackageInstall(activity: Activity) {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:" + activity.packageName)
        activity.startActivityForResult(intent, installRequestCode)
    }

    private fun installApkBelowN(file: File) {
        val context: Context = mRegister.context()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * android7.x (Build.VERSION_CODES.N)及以上安装需要通过 ContentProvider 获取文件Uri，
     * 需在应用中的AndroidManifest.xml 文件添加 provider 标签，
     * 并新增文件路径配置文件 res/xml/provider_path.xml
     */
    private fun installApkAboveN(context: Context, file: File?) {
        if (file == null) {
            Log.e(TAG, "installApkAboveN file is null")
            return
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val uri: Uri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.fileProvider", file)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}
