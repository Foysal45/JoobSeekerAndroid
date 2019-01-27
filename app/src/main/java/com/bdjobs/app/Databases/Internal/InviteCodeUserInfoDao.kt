package com.bdjobs.app.Databases.Internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InviteCodeUserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInviteCodeUserInformation(inviteCodeInfo: InviteCodeInfo)

    @Query("SELECT * FROM InviteCodeInfo WHERE userId =:userId")
    fun getInviteCodeInformation(userId:String): List<InviteCodeInfo>
}