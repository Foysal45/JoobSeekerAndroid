package com.bdjobs.app.databases.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppliedJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppliedJobs(appliedJobs: AppliedJobs)

    @Query("SELECT * FROM AppliedJobs")
    fun getAllAppliedJobs(): List<AppliedJobs>

    @Query("SELECT * FROM AppliedJobs WHERE appliedid =:appliedId")
    fun getAppliedJobsById(appliedId:String?): List<AppliedJobs>

    @Query("DELETE FROM AppliedJobs")
    fun deleteAllAppliedJobs()

    @Query("DELETE FROM AppliedJobs WHERE appliedid =:appliedId")
    fun deleteAppliedJobsByJobID(appliedId:String)

}