#import "FlutterInstallPlugin.h"

@implementation FlutterInstallPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"install_plugin"
                                     binaryMessenger:[registrar messenger]];
    FlutterInstallPlugin* instance = [[FlutterInstallPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"update" isEqualToString:call.method]) {
        [self open:call.arguments result:result];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)open:(id)args result:(FlutterResult)result {
    if (!args || [args isKindOfClass:[NSNull class]]) {
        result([FlutterError errorWithCode:@"-1" message:@"url参数错误: 不能为空" details:@""]);
        return;
    }
    if (![args isKindOfClass:[NSString class]]) {
        result([FlutterError errorWithCode:@"-2" message:@"url参数错误: 类型错误" details:@""]);
        return;
    }

    NSString *urlString = (NSString *)args;
    if (urlString.length == 0) {
        result([FlutterError errorWithCode:@"-1" message:@"url参数错误: 不能为空" details:@""]);
        return;
    }

    NSURL *url = [NSURL URLWithString:urlString];
    if (!url) {
        result([FlutterError errorWithCode:@"-3" message:@"获取URL失败" details:[NSString stringWithFormat:@"URL String: %@", urlString]]);
        return;
    }

    if (![[UIApplication sharedApplication] canOpenURL:url]) {
        result([FlutterError errorWithCode:@"-5" message:@"Url 无法被开启" details:[NSString stringWithFormat:@"URL String: %@", url.absoluteString]]);
        return;
    }

    if (@available(iOS 10.0, *)) {
        [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:^(BOOL success) {
            if (success) {
                result(@"1");
            } else {
                result([FlutterError errorWithCode:@"-4" message:@"Open Url 失败" details:[NSString stringWithFormat:@"URL String: %@", url.absoluteString]]);
            }
        }];
    } else {
        if ([[UIApplication sharedApplication] openURL:url]) {
            result(@"1");
        } else {
            result([FlutterError errorWithCode:@"-4" message:@"Open Url 失败" details:[NSString stringWithFormat:@"URL String: %@", url.absoluteString]]);
        }
    }
}

@end
