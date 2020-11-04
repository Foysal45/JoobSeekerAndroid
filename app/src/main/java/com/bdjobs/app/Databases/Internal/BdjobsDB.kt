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
    VideoInvitation::class,
    LiveInvitation::class,
    B2CCertification::class,
    LastSearch::class,
    InviteCodeInfo::class,
    Notification::class], version = 19, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BdjobsDB : RoomDatabase() {

    abstract fun suggestionDAO(): SuggestionDAO
    abstract fun favouriteSearchFilterDao(): FavouriteSearchFilterDao
    abstract fun followedEmployerDao(): FollowedEmployerDao
    abstract fun shortListedJobDao(): ShortListedJobDao
    abstract fun appliedJobDao(): AppliedJobDao
    abstract fun jobInvitationDao(): JobInvitationDao
    abstract fun videoInvitationDao(): VideoInvitationDao
    abstract fun liveInvitationDao() : LiveInvitationDao
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
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN notification_id TEXT")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `lan_type` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `deadline` TEXT")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE VideoInvitation (id INTEGER, companyName TEXT, jobTitle TEXT, jobId TEXT, videoStatusCode TEXT, videoStatus TEXT, userSeenInterview TEXT, employerSeenDate INTEGER, dateStringForSubmission INTEGER, dateStringForInvitaion INTEGER, PRIMARY KEY(id))")
                database.execSQL("CREATE UNIQUE INDEX `index_VideoInvitation_jobId` ON `VideoInvitation` (`jobId`)")
            }
        }

        val MIGRATION_12_13 = object : Migration(12,13){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        val MIGRATION_13_14 = object : Migration(13,14){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FavouriteSearch ADD COLUMN `isSubscribed` TEXT")
            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE LiveInvitation (id INTEGER, companyName TEXT, jobTitle TEXT, jobId TEXT, liveInterviewStatusCode TEXT, liveInterviewStatus TEXT, userSeenInterview TEXT, liveInterviewDate INTEGER ,liveInterviewTime TEXT, dateStringForInvitaion INTEGER, PRIMARY KEY(id))")
                database.execSQL("CREATE UNIQUE INDEX `index_LiveInvitation_jobId` ON `LiveInvitation` (`jobId`)")
            }
        }

        val MIGRATION_15_16 = object : Migration(15,16){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE LiveInvitation")
            }
        }

        val MIGRATION_16_17 = object : Migration(16,17){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS LiveInvitation")
                database.execSQL("CREATE TABLE LiveInvitation (id INTEGER, companyName TEXT, jobTitle TEXT, jobId TEXT, liveInterviewStatusCode TEXT, liveInterviewStatus TEXT, userSeenLiveInterview TEXT, liveInterviewDate INTEGER ,liveInterviewTime TEXT, dateStringForInvitaion INTEGER, PRIMARY KEY(id))")
                database.execSQL("CREATE UNIQUE INDEX `index_LiveInvitation_jobId` ON `LiveInvitation` (`jobId`)")
            }
        }

        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE LastSearch ADD COLUMN `workPlace` TEXT")
                database.execSQL("ALTER TABLE LastSearch ADD COLUMN `personWithDisability` TEXT")
            }
        }

        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FavouriteSearch ADD COLUMN `workPlace` TEXT")
                database.execSQL("ALTER TABLE FavouriteSearch ADD COLUMN `personWithDisability` TEXT")
            }
        }

        fun getInstance(context: Context): BdjobsDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, BdjobsDB::class.java, internal_database_name).addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                        MIGRATION_12_13,
                        MIGRATION_13_14,
                        MIGRATION_14_15,
                        MIGRATION_15_16,
                        MIGRATION_16_17,
                        MIGRATION_17_18,
                        MIGRATION_18_19
                ).build()
    }
}