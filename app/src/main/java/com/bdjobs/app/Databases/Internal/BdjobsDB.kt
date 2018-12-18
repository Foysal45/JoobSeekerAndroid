package com.bdjobs.app.Databases.Internal

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bdjobs.app.Utilities.Constants.Companion.internal_database_name


@Database(entities = [Suggestion::class,
    FavouriteSearch::class,
    FollowedEmployer::class,
    ShortListedJobs::class,
    AppliedJobs::class,
    JobInvitation::class,
    B2CCertification::class,
    LastSearch::class], version = 1, exportSchema = false)
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


    companion object {
        @Volatile
        private var INSTANCE: BdjobsDB? = null

        fun getInstance(context: Context): BdjobsDB =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, BdjobsDB::class.java, internal_database_name).build()
    }
}