//
//  DetectionViewController.m
//  FaceSDKSample_IOS
//
//  Created by 阿凡树 on 2017/5/23.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import "BDFaceDetectionViewController.h"
#import <IDLFaceSDK/IDLFaceSDK.h>
#import <AVFoundation/AVFoundation.h>
//#import "BDFaceSuccessViewController.h"
#import "BDFaceImageShow.h"
#import "UIColor+BDFaceColorUtils.h"

@interface BDFaceDetectionViewController ()
{
    UIImageView * newImage;
    BOOL isPaint;
}
@property (nonatomic, readwrite, retain) UIView *animaView;
@end
int remindCode = -1;
@implementation BDFaceDetectionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // 纯粹为了在照片成功之后，做闪屏幕动画之用
    self.animaView = [[UIView alloc] initWithFrame:self.view.bounds];
    
    self.animaView.backgroundColor = [UIColor face_colorWithRGBHex:0x101010];
    self.animaView.alpha = 0;
    [self.view addSubview:self.animaView];
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"进入新的界面");
    [[IDLFaceDetectionManager sharedInstance] startInitial];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [[IDLFaceDetectionManager sharedInstance] reset];
}

- (void)onAppWillResignAction {
    [super onAppWillResignAction];
    [IDLFaceDetectionManager.sharedInstance reset];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)faceProcesss:(UIImage *)image {
    if (self.hasFinished) {
        return;
    }
    __weak typeof(self) weakSelf = self;
    [[IDLFaceDetectionManager sharedInstance] detectStratrgyWithNormalImage:image previewRect:self.previewRect detectRect:self.detectRect completionHandler:^(FaceInfo *faceinfo, NSDictionary *images, DetectRemindCode remindCode) {
     
        /*
         此注释里的代码用于显示人脸框，调试过程中需要显示人脸款可打开注释
         
         //      绘制人脸框功能，开发者可以通过观察人脸框_faceRectFit 在  previewRect 包含关系判断是框内还是框外
                 dispatch_async(dispatch_get_main_queue(), ^{
                     CGRect faceRect = [BDFaceQualityUtil getFaceRect:faceinfo.landMarks withCount:faceinfo.landMarks.count];
                     CGRect faceRectFit = [BDFaceUtil convertRectFrom:faceRect image:image previewRect:previewRect];
                     if (!isPaint) {
                         newImage= [[UIImageView alloc]init];
                         [self.view addSubview:newImage];
                         isPaint = !isPaint;
                     }
                     newImage = [self creatRectangle:newImage withRect:faceRectFit  withcolor:[UIColor blackColor]];
                 });
         
         */
         switch (remindCode) {
            case DetectRemindCodeOK: {
                weakSelf.hasFinished = YES;
                
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kVeryGood", nil)];
                if (images[@"image"] != nil && [images[@"image"] count] != 0) {
                    
                    NSArray *imageArr = images[@"image"];
                    for (FaceCropImageInfo * image in imageArr) {
                        NSLog(@"cropImageWithBlack %f %f", image.cropImageWithBlack.size.height, image.cropImageWithBlack.size.width);
                        NSLog(@"originalImage %f %f", image.originalImage.size.height, image.originalImage.size.width);
                        
                    }

                    FaceCropImageInfo * bestImage = imageArr[0];
                    // UI显示选择原图，避免黑框情况
                    [[BDFaceImageShow sharedInstance] setSuccessImage:bestImage.originalImage];
                    [[BDFaceImageShow sharedInstance] setSilentliveScore:bestImage.silentliveScore];
                    
                    NSData *data = UIImageJPEGRepresentation(bestImage.originalImage, 1.0f);
                    NSString * imageStr = [data base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                    if (self.detectOKBlock) {
                        self.detectOKBlock(imageStr);
                    }
                    
                    // 公安验证接口测试，网络接口上传选择扣图，避免占用贷款
                    // [self request:bestImage.cropImageWithBlackEncryptStr];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        UIViewController* fatherViewController = weakSelf.presentingViewController;
                        [weakSelf dismissViewControllerAnimated:YES completion:^{
//                            BDFaceSuccessViewController *avc = [[BDFaceSuccessViewController alloc] init];
//                            avc.modalPresentationStyle = UIModalPresentationFullScreen;
//                            [fatherViewController presentViewController:avc animated:YES completion:nil];
                            [self closeAction];
                        }];                        
                    });
                }
                [self singleActionSuccess:true];
                break;
            }
            case DetectRemindCodeDataHitOne:
                 [self warningStatus:CommonStatus warning:NSLocalizedString(@"kVeryGood", nil)];
                 break;
            case DetectRemindCodePitchOutofDownRange:
                [self warningStatus:PoseStatus warning:NSLocalizedString(@"kLookUp", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodePitchOutofUpRange:
                [self warningStatus:PoseStatus warning:NSLocalizedString(@"kLookDown", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeYawOutofLeftRange:
                [self warningStatus:PoseStatus warning:NSLocalizedString(@"kLookRight", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeYawOutofRightRange:
                [self warningStatus:PoseStatus warning:NSLocalizedString(@"kLookLeft", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodePoorIllumination:
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kLightUp", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeNoFaceDetected:
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kMoveFaceInto", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeImageBlured:
                [self warningStatus:PoseStatus warning:NSLocalizedString(@"kHoldPhone", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionLeftEye:
                 
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskLeftEye", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionRightEye:
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskRightEye", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionNose:
                 
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskNose", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionMouth:
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskMouth", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionLeftContour:
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskLeftFace", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionRightContour:
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskRightFace", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeOcclusionChinCoutour:
                [self warningStatus:occlusionStatus warning:NSLocalizedString(@"kMaskChin", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeTooClose:
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kFaceFurther", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeTooFar:
                 
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kFaceCloser", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeBeyondPreviewFrame:
                 
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kMoveFaceInto", nil)];
                [self singleActionSuccess:false];
                break;
            case DetectRemindCodeVerifyInitError:
                 
                [self warningStatus:CommonStatus warning:NSLocalizedString(@"kAuthFail", nil)];
                break;
            case DetectRemindCodeTimeout: {
                // 时间超时，重置之前采集数据
                 [[IDLFaceDetectionManager sharedInstance] reset];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self isTimeOut:YES];
                });
                break;
            }
            case DetectRemindCodeConditionMeet: {
            }
                break;
            default:
                break;
        }
    }];
}

-(void) saveImage:(UIImage *) image withFileName:(NSString *) fileName{
    
    NSString *docPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    // 2.创建一个文件路径
    NSString *filePath = [docPath stringByAppendingPathComponent:fileName];
    // 3.创建文件首先需要一个文件管理对象
    NSFileManager *fileManager = [NSFileManager defaultManager];
    // 4.创建文件
    [fileManager createFileAtPath:filePath contents:nil attributes:nil];
    NSError * error = nil;
    
    BOOL written = [UIImageJPEGRepresentation(image,1.0f) writeToFile:filePath options:0 error:&error];
    if(!written){
        NSLog(@"write failed %@", [error localizedDescription]);
    }
}

-(void) saveFile:(NSString *) fileName withContent:(NSString *) content{
    NSArray *paths  = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
    NSString *homePath = [paths objectAtIndex:0];
    
    NSString *filePath = [homePath stringByAppendingPathComponent:fileName];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    if(![fileManager fileExistsAtPath:filePath]) //如果不存在
    {
        NSString *str = @"索引 是否活体 活体分值 活体图片路径\n";
        [str writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
    }

    NSFileHandle *fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:filePath];
    [fileHandle seekToEndOfFile];  //将节点跳到文件的末尾
    NSData* stringData  = [content dataUsingEncoding:NSUTF8StringEncoding];
    [fileHandle writeData:stringData]; //追加写入数据
    [fileHandle closeFile];
}

-(NSString *) getCurrentTimes{
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    // ----------设置你想要的格式,hh与HH的区别:分别表示12小时制,24小时制
    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"];
    //现在时间,你可以输出来看下是什么格式
    NSDate *datenow = [NSDate date];
    //----------将nsdate按formatter格式转成nsstring
    NSString *currentTimeString = [formatter stringFromDate:datenow];
    return currentTimeString;
}

- (void) request:(NSString *) imageStr{
    NSError *error;
    NSString *urlString = @"http://10.24.7.251:8316/api/v3/person/verify_sec?appid=7758258";
    NSURL *url = [NSURL URLWithString:urlString];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    NSMutableDictionary * dictionary =  [[NSMutableDictionary alloc] init];
    dictionary[@"risk_identify"] = @(false);
    
    
    dictionary[@"image_type"] = @"BASE64";
    dictionary[@"image"] = imageStr;
    dictionary[@"id_card_number"] = @"请输入你的身份证";
    dictionary[@"name"] = @"请输入你的姓名";
    dictionary[@"quality_control"] = @"NONE";
    dictionary[@"liveness_control"] = @"NONE";
    dictionary[@"risk_identify"] = @YES;
    dictionary[@"zid"] = [[FaceSDKManager sharedInstance] getZtoken];
    dictionary[@"ip"] = @"172.30.154.173";
    dictionary[@"phone"] = @"18610317119";
    dictionary[@"image_sec"] = @NO;
    dictionary[@"app"] = @"ios";
    dictionary[@"ev"] = @"smrz";
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:nil];
    [request setHTTPMethod:@"POST"];
    [request setURL:url];
    [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:jsonData];
    
    NSData *finalDataToDisplay = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:&error];
    NSMutableDictionary *abc = [NSJSONSerialization JSONObjectWithData: finalDataToDisplay
                                                               options: NSJSONReadingMutableContainers
                                                                error: &error];
    NSLog(@"%@", abc);
}

- (void)selfReplayFunction{
    [[IDLFaceDetectionManager sharedInstance] reset];
}
@end
