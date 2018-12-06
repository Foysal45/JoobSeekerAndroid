package com.bdjobs.app.Databases.Internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JobInvitationDao {
    @Query("SELECT * FROM JobInvitation")
    fun getAllJobInvitation(): List<JobInvitation>

    @Query("DELETE FROM JobInvitation")
    fun deleteAllJobInvitation()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobInvitation(jobInvitation: JobInvitation):Long


}