# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/zhangjg/deve/tools/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#####################################################################
#                common rules for java and android                  #
#####################################################################
-keep class com.baidu.vis.unified.license. {*;}
-keep class com.baidu.liantian. {;}
-keep class com.baidu.baidusec.** {;}
-keep class com.baidu.idl.main.facesdk.* {;}