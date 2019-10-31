import 'dart:io';

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_install/flutter_install.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
//    FlutterInstall.install({'filePath': "/storage/emulated/0/Android/data/com.baibao.xxbmm.test/files/xxbmm_1.0.apk", "appId": "com.baibao.xxbmm.test"}
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('FlutterInstallPlugin example app'),
        ),
        body: Center(
          child: FlatButton(onPressed: update, child: Text("install")),
        ),
      ),
    );
  }

  void checkToInstall() async {
    Map<PermissionGroup, PermissionStatus> resultMap =
    await PermissionHandler().requestPermissions(<PermissionGroup>[
      PermissionGroup.storage,
    ]);
    if (resultMap.containsKey(PermissionGroup.storage) &&
        resultMap[PermissionGroup.storage] == PermissionStatus.granted) {
      FlutterInstall.installApk(
        "/storage/emulated/0/Android/data/com.baibao.xxbmm.test/files/xxbmm_1.0.apk",
      ).then((result) {
        print("result: $result");
      });
    } else {
      print("storage permisson denied");
    }
  }

  void update() {
    if (Platform.isAndroid) {
      checkToInstall();
    } else if (Platform.isIOS) {
      FlutterInstall.update("https://itunes.apple.com/cn/app/%E5%86%8D%E6%83%A0%E5%90%88%E4%BC%99%E4%BA%BA/id1375433239?l=zh&ls=1&mt=8");
    }
  }
}
