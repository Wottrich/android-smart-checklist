package wottrich.github.io.smartchecklist.backup.google

/**
 * Constants of the Google Drive backup feature.
 *
 * TODO(Step 0 — repo owner): set the real **Web application** OAuth client ID here
 *  (Google Cloud Console → APIs & Services → Credentials). It is required by
 *  Credential Manager even for Android-only apps and is public by design.
 *  Also create Android OAuth clients for the debug/release SHA-1 fingerprints and
 *  enable the Google Drive API + configure the OAuth consent screen with the
 *  `https://www.googleapis.com/auth/drive.appdata` scope. See
 *  docs/plans/google-drive-backup.md → Step 0.
 */
object GoogleDriveBackupConfig {

    /** Web (server) OAuth client ID used by Credential Manager account selection. */
    const val WEB_CLIENT_ID = ""

    /** Hidden file name inside the Drive `appDataFolder`. */
    const val BACKUP_FILE_NAME = "smart-checklist-backup.json"

    /** Application name sent with every Drive request. */
    const val APPLICATION_NAME = "Smart Checklist"
}
