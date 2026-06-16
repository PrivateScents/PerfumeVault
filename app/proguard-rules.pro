# Project specific ProGuard rules

# Keep Room entities and DAOs
-keep class com.perfumevault.data.** { *; }
-dontwarn com.perfumevault.data.**

# Keep Compose internal names for debugging (optional but helpful)
-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations

# Coil
-keep class coil.** { *; }
-dontwarn coil.**
