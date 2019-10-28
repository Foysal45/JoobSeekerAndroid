package com.bdjobs.app.Databases.Internal

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bdjobs.app.Utilities.Constants.Companion.internal_database_name


@Database(entities = [Suggestion::class,
    FavouriteSearch::class,
    FollowedEmployer::class,
    ShortListedJobs::class,
    AppliedJobs::class,
    JobInvitation::class,
    B2CCertification::class,
    LastSearch::class,
    InviteCodeInfo::class,
    Notification::class], version = 9, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BdjobsDB : RoomDatabase() {

    abstract fun suggestionDAO(): SuggestionDAO
    abstract fun favouriteSearchFilterDao(): FavouriteSearchFilterDao
    abstract fun followedEmployerDao(): FollowedEmployerDao
    abstract fun shortListedJobDao(): ShortListedJobDao
    abstract fun appliedJobDao(): AppliedJobDao
    abstract fun jobInvitationDao(): JobInvitationDao
    abstract fun b2CCertificationDao(): B2CCertificationDao
    abstract fun lastSearchDao(): LastSearchDao
    abstract fun inviteCodeUserInfoDao(): InviteCodeUserInfoDao
    abstract fun notificationDao(): NotificationDao


    companion object {
        @Volatile
        private var INSTANCE: BdjobsDB? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `Notification` " +
                        "(`id` INTEGER, `type` TEXT, `seen` TINYINT, `arrival_time` INTEGER," +
                        "`seen_time` INTEGER, `payload` TEXT, " +
                        "PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN server_id TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `img_link` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `link` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `is_deleted` TINYINT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `job_title` TEXT")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `title` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `body` TEXT")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `company_name` TEXT")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE JobInvitation")
                database.execSQL("CREATE TABLE JobInvitation (id INTEGER, companyName TEXT, inviteDate INTEGER, jobId TEXT, jobTitle TEXT, seen TEXT, PRIMARY KEY(id))")
                database.execSQL("CREATE UNIQUE INDEX `index_JobInvitation_jobId` ON `JobInvitation` (`jobId`)")

//                database.execSQL("INSERT INTO JobInvitation_new (id, companyName, inviteDate, jobId, jobTitle) SELECT id, companyName, inviteDate, jobId, jobTitle FROM JobInvitation")
//
//                database.execSQL("ALTER TABLE JobInvitation_new RENAME TO JobInvitation")
            }
        }

        fun getInstance(context: Context): BdjobsDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, BdjobsDB::class.java, internal_database_name).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9).build()
    }
}