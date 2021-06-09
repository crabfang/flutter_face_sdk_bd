#import "WidgetFaceSdkPlugin.h"
#import "IDLFaceSDK/IDLFaceSDK.h"
#import "FaceParameterConfig.h"
#import "BDFaceDetectionViewController.h"
#import "BDFaceLivenessViewController.h"
#import "BDFaceLivingConfigModel.h"
#import "BDFaceAdjustParamsTool.h"

@implementation WidgetFaceSdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"FaceSDKPlugin"
            binaryMessenger:[registrar messenger]];
  WidgetFaceSdkPlugin* instance = [[WidgetFaceSdkPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"init" isEqualToString:call.method]) {
      NSString *licenseID = call.arguments[@"licenseID"];
      NSString *licenseFileName = call.arguments[@"licenseFileName"];
      NSInteger qualityLevel = [call.arguments[@"qualityLevel"] integerValue];
      BOOL isOpenSound = [call.arguments[@"isOpenSound"] boolValue];
      BOOL remoteAuthorize = [call.arguments[@"remoteAuthorize"] boolValue];
      
//      licenseID = FACE_LICENSE_ID;
//      licenseFileName = [NSString stringWithFormat:@"%@.%@", FACE_LICENSE_NAME, FACE_LICENSE_SUFFIX ];
      
      [[FaceSDKManager sharedInstance] setLicenseID:licenseID andLocalLicenceFile:licenseFileName andRemoteAuthorize:remoteAuthorize];
      
      //init SDK
      [self initSDKWithQualityLevel:qualityLevel];
      [self initLivenesswithList];
      
      if ([[FaceSDKManager sharedInstance] canWork]) {
          if (isOpenSound) {
              // 活体声音
              [IDLFaceLivenessManager sharedInstance].enableSound  = YES;
              // 图像采集声音
              [IDLFaceDetectionManager sharedInstance].enableSound = YES;
//              NSLog(@"打开了声音");
          } else {
              // 活体声音
              [IDLFaceLivenessManager sharedInstance].enableSound  = NO;
              // 图像采集声音
              [IDLFaceDetectionManager sharedInstance].enableSound = NO;
//              NSLog(@"关闭了声音");
          }
          
          result(@{@"code" : @(0)});
      } else {
          result(@{@"code" : @(1)});
      }
      
  } else if ([@"startVerify" isEqualToString:call.method]) {
      BOOL isAlive = [call.arguments[@"isAlive"] boolValue];
      
      if (!isAlive) {//人脸
          BDFaceDetectionViewController* dvc = [[BDFaceDetectionViewController alloc] init];
          dvc.detectOKBlock = ^(NSString *imageStr) {
              result(@{@"image" : imageStr});
          };
          UINavigationController *navi = [[UINavigationController alloc] initWithRootViewController:dvc];
          navi.navigationBarHidden = true;
          navi.modalPresentationStyle = UIModalPresentationFullScreen;
          UIWindow* window = [[[UIApplication sharedApplication] delegate] window];
          
          [window.rootViewController presentViewController:navi animated:YES completion:nil];
      } else {//活体
          BDFaceLivenessViewController* lvc = [[BDFaceLivenessViewController alloc] init];
          lvc.detectOKBlock = ^(NSString *imageStr) {
              result(@{@"image" : imageStr});
          };
          BDFaceLivingConfigModel* model = [BDFaceLivingConfigModel sharedInstance];
          [lvc livenesswithList:model.liveActionArray order:model.isByOrder numberOfLiveness:model.numOfLiveness];
          UINavigationController *navi = [[UINavigationController alloc] initWithRootViewController:lvc];
          navi.navigationBarHidden = true;
          navi.modalPresentationStyle = UIModalPresentationFullScreen;
          UIWindow* window = [[[UIApplication sharedApplication] delegate] window];
          [window.rootViewController presentViewController:navi animated:YES completion:nil];
      }
  }
}

- (void)initSDKWithQualityLevel:(NSInteger)qualityLevel {
    if ([[FaceSDKManager sharedInstance] canWork]){
        // 初始化SDK配置参数，可使用默认配置
        // 设置最小检测人脸阈值
        [[FaceSDKManager sharedInstance] setMinFaceSize:200];
        // 设置截取人脸图片高
        [[FaceSDKManager sharedInstance] setCropFaceSizeWidth:480];
        // 设置截取人脸图片宽
        [[FaceSDKManager sharedInstance] setCropFaceSizeHeight:640];
        // 设置人脸遮挡阀值
        [[FaceSDKManager sharedInstance] setOccluThreshold:0.5];
        // 设置亮度阀值
        [[FaceSDKManager sharedInstance] setMinIllumThreshold:40];
        [[FaceSDKManager sharedInstance] setMaxIllumThreshold:240];
        // 设置图像模糊阀值
        [[FaceSDKManager sharedInstance] setBlurThreshold:0.3];
        // 设置头部姿态角度
        [[FaceSDKManager sharedInstance] setEulurAngleThrPitch:10 yaw:10 roll:10];
        // 设置人脸检测精度阀值
        [[FaceSDKManager sharedInstance] setNotFaceThreshold:0.6];
        // 设置抠图的缩放倍数
        [[FaceSDKManager sharedInstance] setCropEnlargeRatio:2.5];
        // 设置照片采集张数
        [[FaceSDKManager sharedInstance] setMaxCropImageNum:3];
        // 设置超时时间
        [[FaceSDKManager sharedInstance] setConditionTimeout:15];
        // 设置开启口罩检测，非动作活体检测可以采集戴口罩图片
        [[FaceSDKManager sharedInstance] setIsCheckMouthMask:true];
        // 设置开启口罩检测情况下，非动作活体检测口罩过滤阈值，默认0.8 不需要修改
        [[FaceSDKManager sharedInstance] setMouthMaskThreshold:0.8f];
        // 设置原始图缩放比例
        [[FaceSDKManager sharedInstance] setImageWithScale:0.8f];
        // 设置图片加密类型，type=0 基于base64 加密；type=1 基于百度安全算法加密
        [[FaceSDKManager sharedInstance] setImageEncrypteType:0];
        // 初始化SDK功能函数
        [[FaceSDKManager sharedInstance] initCollect];
        // 设置人脸过远框比例
        [[FaceSDKManager sharedInstance] setMinRect:0.4];
        
        // 设置用户设置的配置参数
        [BDFaceAdjustParamsTool setDefaultConfig];
    }
}

- (void)initLivenesswithList {
    // 默认活体检测打开，顺序执行
    /*
     添加当前默认的动作，是否需要按照顺序，动作活体的数量（设置页面会根据这个numOfLiveness来判断选择了几个动作）
     */
    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLiveEye)];
    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLiveMouth)];
    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLiveYawRight)];
//    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLiveYawLeft)];
//    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLivePitchUp)];
//    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLivePitchDown)];
//    [BDFaceLivingConfigModel.sharedInstance.liveActionArray addObject:@(FaceLivenessActionTypeLiveYaw)];
    BDFaceLivingConfigModel.sharedInstance.isByOrder = NO;
    BDFaceLivingConfigModel.sharedInstance.numOfLiveness = 3;
}


@end
