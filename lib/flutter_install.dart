import 'dart:async';

import 'package:flutter/services.dart';

class FlutterInstall {
  static const MethodChannel _channel = const MethodChannel('install_plugin');

  /// for Android: update apk
  static Future<String?> updateApk(String url, String? title) async {
    Map<String, dynamic> params = {'url': url, 'title': title};
    return await _channel.invokeMethod('downloadApk', params);
  }

  /// for iOS: check update
  static Future<String?> update(String url) async {
    return await _channel.invokeMethod("update", url);
  }
}
