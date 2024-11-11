# Obfuscation parameters:
#-dontobfuscate
-useuniqueclassmembernames
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification

-dontwarn org.joda.convert.**

-keep class com.sap.ultralitejni17.** { *; }
-keep class android.support.multidex.** { *; }
-keep class com.synchroteam.** { *; }
