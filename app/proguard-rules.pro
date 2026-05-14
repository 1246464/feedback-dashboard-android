# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep application classes
-keep class com.example.visualizadorapp.** { *; }

# Keep all activities
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity

# Keep ViewModels
-keep public class * extends androidx.lifecycle.ViewModel { *; }
-keep public class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep Repository classes
-keep class com.example.visualizadorapp.repository.** { *; }

# Keep Model/Entity classes
-keep class com.example.visualizadorapp.model.** { *; }

# Keep DAO classes
-keep class com.example.visualizadorapp.database.** { *; }

# Keep Utility classes
-keep class com.example.visualizadorapp.util.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }

# Keep Firebase Database
-keep class com.google.firebase.database.** { *; }
-keep class com.google.firebase.auth.** { *; }

# Keep Material Design
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }

# Keep AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep Google Play Services
-keep class com.google.android.gms.** { *; }

# Keep Room Database
-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }

# Keep methods that might be called via reflection
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keep class * implements java.io.Serializable { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile