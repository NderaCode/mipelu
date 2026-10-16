# R8 configuration for release builds.
#
# Most of this project's libraries (OkHttp, Retrofit, Hilt, Coil, AndroidX) ship their own
# consumer rules, so what's left here is the two things R8 cannot infer on its own:
# kotlinx.serialization's generated serializers, and readable crash reports.

# --- Crash reports ---------------------------------------------------------------------------
# Without these, every stack trace from a release build is unreadable line-number soup. The
# mapping file (build/outputs/mapping/release/mapping.txt) still has to be archived per release
# to deobfuscate them; -renamesourcefileattribute avoids leaking original file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization -------------------------------------------------------------------
# Serializers are looked up reflectively via the generated $$serializer classes and the companion
# object's serializer() method. R8 sees no call site for either, strips them, and the app then
# fails at runtime on the first API response - not at build time, which is what makes this the
# classic "release build is broken but debug works" bug.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.cocido.mipelu.**$$serializer { *; }
-keepclassmembers class com.cocido.mipelu.** {
    *** Companion;
}
-keepclasseswithmembers class com.cocido.mipelu.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable data classes are instantiated reflectively by the generated serializer.
-keepclassmembers @kotlinx.serialization.Serializable class com.cocido.mipelu.** {
    <fields>;
}

# --- Tink (androidx.security.crypto, used by TokenStore) --------------------------------------
# Tink is annotated with Error Prone annotations that are compile-only and deliberately absent at
# runtime. They carry no behaviour, so R8 can be told to stop treating their absence as fatal.
-dontwarn com.google.errorprone.annotations.**

# --- Retrofit --------------------------------------------------------------------------------
# R8 full mode (the default since AGP 8) erases generic signatures, which Retrofit needs to work
# out the response type of each suspend function.
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
