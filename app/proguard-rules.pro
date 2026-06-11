# Hermes Android ProGuard Rules

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.hermes.android.HermesApplication { *; }

# Keep Room entities and DAOs
-keep class com.hermes.android.data.local.db.** { *; }
-keep class com.hermes.android.data.local.db.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep DataStore preferences
-keep class com.hermes.android.data.local.preferences.** { *; }

# Keep Retrofit/Gson serialization
-keep class com.hermes.android.data.remote.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature,EnclosingMethod,InnerClasses

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep Accompanist
-keep class com.google.accompanist.** { *; }

# Keep Termux related
-keep class com.hermes.android.data.local.termux.** { *; }

# Keep ViewModels
-keep class com.hermes.android.presentation.viewmodel.** { *; }

# Keep UseCases
-keep class com.hermes.android.domain.usecase.** { *; }

# Keep Repositories
-keep class com.hermes.android.domain.repository.** { *; }
-keep class com.hermes.android.data.repository.** { *; }

# Application class
-keep class com.hermes.android.HermesApplication { *; }

# Main Activity
-keep class com.hermes.android.presentation.ui.MainActivity { *; }

# Gson
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# OkHttp EventSource (SSE)
-keep class okhttp3.sse.** { *; }

# Prevent obfuscation of enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}