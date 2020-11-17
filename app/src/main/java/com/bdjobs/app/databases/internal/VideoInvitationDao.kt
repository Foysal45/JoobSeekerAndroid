package com.bdjobs.app.databases.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface VideoInvitationDao {
    @Query("SELECT * FROM VideoInvitation")
    fun getAllVideoInvitation(): List<VideoInvitation>

    @Query("SELECT * FROM VideoInvitation WHERE dateStringForInvitaion>=:dt")
    fun getALLVideoInvitationByDate(dt:Date): List<VideoInvitation>


    @Query("DELETE FROM VideoInvitation")
    fun deleteAllVideoInvitation()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoInvitation(videoInvitation: VideoInvitation):Long


}