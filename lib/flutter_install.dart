import 'dart:async';

import 'package:flutter/services.dart';

class FlutterInstall {
  static const MethodChannel _channel = const MethodChannel('install_plugin');

  /// for Android: install apk by its file absolute path
  static Future<String> installApk(filePath) async {
    Map<String, dynamic> params = {"filePath": filePath};
    return await _channel.invokeMethod('installApk', params);
  }

  /// for iOS: check update
  static Future<String> update(String url) async{
    Map<String, dynamic> params = {"url": url};
    return await _channel.invokeMethod("update", params);
  }
}
