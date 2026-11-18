# Google Drive backup (issue #94).
# kotlinx-serialization ships consumer rules for @Serializable classes; keep the
# backup schema explicitly so restore survives R8 in minified release builds.
-keepclassmembers class wottrich.github.io.smartchecklist.backup.data.backupfile.** {
    *** Companion;
}
-keepclasseswithmembers class wottrich.github.io.smartchecklist.backup.data.backupfile.** {
    kotlinx.serialization.KSerializer serializer(...);
}
