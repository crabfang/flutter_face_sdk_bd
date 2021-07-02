//
//  NSBundle+Plugin.m
//  Pods
//
//  Created by Howie Xu on 2021/6/28.
//

#import "NSBundle+Plugin.h"
#import "PluginConfig.h"
#import <objc/runtime.h>

@interface PluginBundle : NSBundle

@end

@implementation NSBundle (Plugin)

- (NSString *)sdkplugin_pathForResource:(NSString *)name ofType:(NSString *)ext {
    NSString *tempName = name;
    if ([ext isEqualToString:@"mp3"]) {
        NSArray *whiteArray = @[@"blink", @"ding", @"head_down", @"head_up", @"head_yaw", @"moveFace", @"open_mouth", @"turn_left", @"turn_right"];
        for (NSString *str in whiteArray) {
            if ([name isEqualToString:str]) {
                tempName = [tempName stringByAppendingString:@"_en"];
            }
        }
        
//        NSLog(@"---- name %@   %@", tempName, ext);
    }
    
    return [self sdkplugin_pathForResource:name ofType:ext];
}


+ (BOOL)isChineseLanguage {
    NSString *currentLanguage = [self currentLanguage];
    if ([currentLanguage hasPrefix:@"zh-Hans"]) {
        return YES;
    } else {
        return NO;
    }
}

+ (NSString *)currentLanguage {
    return [PluginConfig userLanguage] ? : [NSLocale preferredLanguages].firstObject;
}

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //动态继承、交换，方法类似KVO，通过修改[NSBundle mainBundle]对象的isa指针，使其指向它的子类DABundle，这样便可以调用子类的方法；其实这里也可以使用method_swizzling来交换mainBundle的实现，来动态判断，可以同样实现。
        object_setClass([NSBundle mainBundle], [PluginBundle class]);
    });
}

@end

@implementation PluginBundle

- (NSString *)localizedStringForKey:(NSString *)key value:(NSString *)value table:(NSString *)tableName {
    if ([PluginBundle uw_mainBundle]) {
        return [[PluginBundle uw_mainBundle] localizedStringForKey:key value:value table:tableName];
    } else {
        return [super localizedStringForKey:key value:value table:tableName];
    }
}

+ (NSBundle *)uw_mainBundle {
    if ([NSBundle currentLanguage].length) {
        NSString *path = [[NSBundle mainBundle] pathForResource:[NSBundle currentLanguage] ofType:@"lproj"];
        if (path.length) {
            return [NSBundle bundleWithPath:path];
        }
    }
    return nil;
}

@end
