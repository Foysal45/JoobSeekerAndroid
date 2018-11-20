package com.bdjobs.app.Databases.Internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShortListedJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShortListedJob(shortListedJobs: ShortListedJobs)

    @Query("SELECT * FROM ShortListedJobs ")
    fun getAllShortListedJobs(): List<ShortListedJobs>

    @Query("DELETE FROM ShortListedJobs")
    fun deleteAllShortListedJobs()

    @Query("DELETE FROM ShortListedJobs WHERE jobid =:jobId")
    fun deleteShortListedJobsByJobID(jobId:String)




}