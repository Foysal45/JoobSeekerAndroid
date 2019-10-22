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
    Notification::class], version = 5, exportSchema = false)
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

        val MIGRATION_3_4 = object : Migration(3,4){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `img_link` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `link` TEXT")
                database.execSQL("ALTER TABLE Notification ADD COLUMN `is_deleted` TINYINT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4,5){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notification ADD COLUMN `job_title` TEXT")
            }
        }

        fun getInstance(context: Context): BdjobsDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, BdjobsDB::class.java, internal_database_name).addMigrations(MIGRATION_1_2, MIGRATION_2_3,MIGRATION_3_4, MIGRATION_4_5).build()
    }
}