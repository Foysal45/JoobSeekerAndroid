package com.bdjobs.app.Databases.Internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface LiveInvitationDao {
    @Query("SELECT * FROM LiveInvitation")
    fun getAllVideoInvitation(): List<LiveInvitation>

    @Query("SELECT * FROM LiveInvitation WHERE dateStringForInvitaion>=:dt")
    fun getAllLiveInvitationByDate(dt:Date): List<LiveInvitation>


    @Query("DELETE FROM LiveInvitation")
    fun deleteAllLiveInvitation()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLiveInvitation(liveInvitation: LiveInvitation):Long


}