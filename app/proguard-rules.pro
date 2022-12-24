# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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
-keepattributes Signature
-keepattributes *Annotation*

-keep class ma.wanam.youtubeadaway.XChecker { *; }

-keep class * implements de.robv.android.xposed.IXposedHookZygoteInit
-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage
-keep class * implements de.robv.android.xposed.IXposedHookInitPackageResources
-keep class * extends de.robv.android.xposed.XC_MethodHook
-keep class * extends de.robv.android.xposed.XC_MethodReplacement

-keepclassmembers class * implements de.robv.android.xposed.IXposedHookZygoteInit {
   public void initZygote(de.robv.android.xposed.IXposedHookZygoteInit$StartupParam);
}

-keepclassmembers class * implements de.robv.android.xposed.IXposedHookLoadPackage {
   public void handleLoadPackage(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam);
}

-keepclassmembers class * implements de.robv.android.xposed.IXposedHookInitPackageResources {
   public void handleInitPackageResources(de.robv.android.xposed.callbacks.XC_InitPackageResources$InitPackageResourcesParam);
}

-keepclassmembers class * extends de.robv.android.xposed.XC_MethodHook {
   protected void beforeHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam);
   protected void afterHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam);
}

-keepclassmembers class * extends de.robv.android.xposed.XC_MethodReplacement {
   protected void replaceHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam);
}
