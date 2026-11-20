package wottrich.github.io.smartchecklist.share.host.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The **owner-side** host database — a dedicated Room file, deliberately isolated
 * from the personal `checklistDatabase2020`: it holds only what the embedded server
 * serves to guests (change log, state mirror, shares, guests, tombstones) and can
 * be dropped when nothing is shared anymore. Main-Room migrations stay untouched.
 */
@Database(
    entities = [
        ShareDTO::class,
        ShareGuestDTO::class,
        ShareMemberDTO::class,
        ShareChangeLogDTO::class,
        HostChecklistDTO::class,
        HostTaskDTO::class,
        ShareTombstoneDTO::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class ShareHostDatabase : RoomDatabase() {

    abstract fun shareDao(): ShareDao

    companion object {

        const val DATABASE_NAME = "share_host"

        @Volatile
        private var instance: ShareHostDatabase? = null

        fun getInstance(context: Context): ShareHostDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ShareHostDatabase {
            return Room.databaseBuilder(context, ShareHostDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        /**
         * v2 hardening: guests move to the pairing lifecycle (hashed token, expiry,
         * revocation). Drop + recreate — v1 guests were self-registered and
         * unapproved by definition; they must re-pair, which is the point.
         * Owner loopback re-pairs implicitly on the next `share()` call.
         */
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `guests`")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `guests` (" +
                        "`user_id` TEXT NOT NULL PRIMARY KEY, " +
                        "`token_hash` TEXT NOT NULL, " +
                        "`name` TEXT, " +
                        "`approved_at` INTEGER NOT NULL, " +
                        "`last_seen_at` INTEGER NOT NULL, " +
                        "`expires_at` INTEGER NOT NULL, " +
                        "`revoked` INTEGER NOT NULL DEFAULT 0)"
                )
                db.execSQL("ALTER TABLE `shares` ADD COLUMN `key_version` INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}
