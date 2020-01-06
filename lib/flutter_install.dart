import 'dart:async';

import 'package:flutter/services.dart';

class FlutterInstall {
  static const MethodChannel _channel = const MethodChannel('install_plugin');

  /// for Android: update apk
  static Future<String> updateApk(filePath) async {
    Map<String, dynamic> params = {"url": filePath};
    return await _channel.invokeMethod('downloadApk', params);
  }

  /// for iOS: check update
  static Future<String> update(String url) async{
    Map<String, dynamic> params = {"url": url};
    return await _channel.invokeMethod("update", params);
  }
}
