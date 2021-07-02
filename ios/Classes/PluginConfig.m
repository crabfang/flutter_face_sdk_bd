//
//  PluginConfig.m
//  Pods
//
//  Created by Howie Xu on 2021/6/29.
//

#import "PluginConfig.h"

static NSString *const PluginUserLanguageKey = @"PluginUserLanguageKey";

@implementation PluginConfig

+ (void)setUserLanguage:(NSString *)userLanguage {
    //跟随手机系统
    if (!userLanguage.length) {
        [self resetSystemLanguage];
        return;
    }
    //用户自定义
    [[NSUserDefaults standardUserDefaults] setValue:userLanguage forKey:PluginUserLanguageKey];
//    [[NSUserDefaults standardUserDefaults] setValue:@[userLanguage] forKey:@"AppleLanguages"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (NSString *)userLanguage
{
    return [[NSUserDefaults standardUserDefaults] valueForKey:PluginUserLanguageKey];
}

/**
 重置系统语言
 */
+ (void)resetSystemLanguage
{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:PluginUserLanguageKey];
//    [[NSUserDefaults standardUserDefaults] setValue:nil forKey:@"AppleLanguages"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

@end
