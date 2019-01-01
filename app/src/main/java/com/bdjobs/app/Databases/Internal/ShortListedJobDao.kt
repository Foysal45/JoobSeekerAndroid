package com.bdjobs.app.Databases.Internal

import androidx.room.*

@Dao
interface ShortListedJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShortListedJob(shortListedJobs: ShortListedJobs)

    @Query("SELECT * FROM ShortListedJobs ")
    fun getAllShortListedJobs(): List<ShortListedJobs>

    @Query("DELETE FROM ShortListedJobs")
    fun deleteAllShortListedJobs()

    @Query("DELETE FROM ShortListedJobs WHERE jobid =:jobId")
    fun deleteShortListedJobsByJobID(jobId: String)

    @Query("SELECT * FROM ShortListedJobs WHERE jobid =:jobId")
    fun getShortListedJobById(jobId: String): List<ShortListedJobs>

    @Transaction
    fun isItShortListed(jobId: String): Boolean {
        val jobs = getShortListedJobById(jobId)
        if (jobs.isNullOrEmpty()) {
            return false
        }
        return true
    }


}