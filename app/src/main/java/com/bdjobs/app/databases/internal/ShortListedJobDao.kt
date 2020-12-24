package com.bdjobs.app.databases.internal

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
    fun getShortListedJobById(jobId: String?): List<ShortListedJobs>

    @Query("SELECT * FROM ShortListedJobs WHERE deadline<=:deadline and deadline>:yesterday ORDER BY id DESC")
    fun getAllShortListedJobsByDeadline(deadline:Date,yesterday:Date): List<ShortListedJobs>

    @Transaction
    fun isItShortListed(jobId: String?): Boolean {
        val jobs = getShortListedJobById(jobId)
        if (jobs.isNullOrEmpty()) {
            return false
        }
        return true
    }

    @Transaction
    fun getShortListedJobsBYDeadline(deadline:Date): List<ShortListedJobs>{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        return getAllShortListedJobsByDeadline(deadline,yesterday)
    }


}