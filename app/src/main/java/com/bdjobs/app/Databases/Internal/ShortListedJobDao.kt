package com.bdjobs.app.Databases.Internal

import androidx.room.*
import java.util.*

@Dao
interface ShortListedJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShortListedJob(shortListedJobs: ShortListedJobs)

    @Query("SELECT * FROM ShortListedJobs ORDER BY id DESC")
    fun getAllShortListedJobs(): List<ShortListedJobs>

    @Query("DELETE FROM ShortListedJobs")
    fun deleteAllShortListedJobs()

    @Query("DELETE FROM ShortListedJobs WHERE jobid =:jobId")
    fun deleteShortListedJobsByJobID(jobId: String)

    @Query("SELECT * FROM ShortListedJobs WHERE jobid =:jobId")
    fun getShortListedJobById(jobId: String): List<ShortListedJobs>

    @Query("SELECT * FROM ShortListedJobs WHERE deadline<=:deadline and deadline>=:today ORDER BY id DESC")
    fun getAllShortListedJobsByDeadline(deadline:Date,today:Date): List<ShortListedJobs>

    @Transaction
    fun isItShortListed(jobId: String): Boolean {
        val jobs = getShortListedJobById(jobId)
        if (jobs.isNullOrEmpty()) {
            return false
        }
        return true
    }

    @Transaction
    fun getShortListedJobsBYDeadline(deadline:Date): List<ShortListedJobs>{
        return getAllShortListedJobsByDeadline(deadline,Date())
    }


}