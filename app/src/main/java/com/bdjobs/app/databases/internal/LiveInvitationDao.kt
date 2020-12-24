package com.bdjobs.app.databases.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface LiveInvitationDao {
    @Query("SELECT * FROM LiveInvitation")
    fun getAllLiveInvitation(): List<LiveInvitation>

    @Query("SELECT * FROM LiveInvitation WHERE liveInterviewDate>=:dt")
    fun getAllLiveInvitationByDate(dt:Date): List<LiveInvitation>

    @Query("DELETE FROM LiveInvitation")
    fun deleteAllLiveInvitation()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLiveInvitation(liveInvitation: LiveInvitation):Long

    @Query("SELECT * FROM liveinvitation WHERE liveInterviewDate BETWEEN :from AND :to")
    fun getTodaysInvitation(from : Date, to : Date): List<LiveInvitation>

}