import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_install/flutter_install.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('FlutterInstallPlugin example app'),
        ),
        body: Center(
          child: FlatButton(onPressed: update, child: Text('install')),
        ),
      ),
    );
  }

  void checkToInstall() async {
    if (await Permission.storage.request().isGranted) {
      FlutterInstall.updateApk(
          'https://f.meishi.51baibao.com/mt/e07f374c-0aa9-11ea-b032-0242ac1e0002/xxbmm--v1.8.0-4.apk',
          '测试名称'
      ).then((result) {
        print('result: $result');
      });
    }
  }

  void update() {
    if (Platform.isAndroid) {
      checkToInstall();
    } else if (Platform.isIOS) {
      FlutterInstall.update('https://itunes.apple.com/cn/app/%E5%86%8D%E6%83%A0%E5%90%88%E4%BC%99%E4%BA%BA/id1375433239?l=zh&ls=1&mt=8');
    }
  }
}
