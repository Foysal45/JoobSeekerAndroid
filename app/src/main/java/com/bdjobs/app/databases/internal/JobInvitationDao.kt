package com.bdjobs.app.databases.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface JobInvitationDao {
    @Query("SELECT * FROM JobInvitation")
    fun getAllJobInvitation(): List<JobInvitation>

    @Query("SELECT * FROM JobInvitation WHERE inviteDate>=:dt")
    fun getALLJobInvitationByDate(dt:Date): List<JobInvitation>


    @Query("DELETE FROM JobInvitation")
    fun deleteAllJobInvitation()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobInvitation(jobInvitation: JobInvitation):Long

    @Query("SELECT * FROM JobInvitation WHERE interviewDate BETWEEN :from AND :to")
    fun getTodaysInvitation(from : Date, to : Date): List<JobInvitation>

}