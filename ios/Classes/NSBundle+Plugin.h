//
//  NSBundle+Plugin.h
//  Pods
//
//  Created by Howie Xu on 2021/6/28.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSBundle (Plugin)

- (NSString *)sdkplugin_pathForResource:(NSString *)name ofType:(NSString *)ext;

+ (BOOL)isChineseLanguage;

+ (NSString *)currentLanguage;

@end

NS_ASSUME_NONNULL_END
