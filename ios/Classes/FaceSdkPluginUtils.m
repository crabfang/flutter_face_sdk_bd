//
//  FaceSdkPluginUtils.m
//  widget_face_sdk
//
//  Created by Howie Xu on 2021/6/16.
//

#import "FaceSdkPluginUtils.h"

@implementation FaceSdkPluginUtils

+ (NSString *)pluginStringWithKey:(NSString *)key {
    if (!key || key.length == 0) {
        return @"";
    }
    NSString *path = [[NSBundle mainBundle] pathForResource:@"FaceSdkPluginUIStrings" ofType:@"plist"];
    NSDictionary * dict = [[NSDictionary alloc]initWithContentsOfFile:path];
    return [dict objectForKey:key];
}

@end
