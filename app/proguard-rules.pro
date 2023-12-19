# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-ignorewarnings
-keep class com.elementary.mualijpro.ui.fragments.profile.ProfileFragment**

-keep class com.elementary.mualijpro.models.*{}
-dontwarn com.elementary.mualijpro.models.**

-keep class com.github.*{}
-dontwarn com.github.**

-keep class com.tapadoor.*{}
-dontwarn com.tapadoo.**

#-keep class com.github.khizar1556.*{}
#-dontwarn com.github.khizar1556.**
#-keep class com.khizar1556.mkvideoplayer.MKPlayerActivity**

-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
    public <init>(...);
 }

-keepattributes SourceFile, Exceptions, Signature, InnerClasses, LineNumberTable

#-assumenosideeffects class java.io.PrintStream {
#     public void println(%);
#     public void println(**);
# }
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
# }





