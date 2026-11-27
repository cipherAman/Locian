# ── Locian Native JNI Bridge ──────────────────────────────────────────────────
-keep class io.locian.inference.** { *; }
-keepclassmembers class io.locian.inference.** {
    native <methods>;
}

# ── Hilt / Dagger ─────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ── Room ──────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }

# ── Kotlin Serialization ──────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <methods>;
}

# ── OkHttp ────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.** { *; }

# ── Markwon Markdown ──────────────────────────────────────────────────────────
-keep class io.noties.markwon.** { *; }
-dontwarn io.noties.**

# ── Coil Image Loading ────────────────────────────────────────────────────────
-keep class coil3.** { *; }
-dontwarn coil3.**

# ── Compose ───────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Suppress transitively missing classes ─────────────────────────────────────
-dontwarn java.lang.instrument.**
-dontwarn javax.annotation.**
