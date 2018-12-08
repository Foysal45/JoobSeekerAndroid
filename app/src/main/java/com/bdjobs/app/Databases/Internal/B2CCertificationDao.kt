package com.bdjobs.app.Databases.Internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface B2CCertificationDao {
    @Query("SELECT * FROM B2CCertification")
    fun getAllB2CCertification(): List<B2CCertification>

    @Query("DELETE FROM B2CCertification")
    fun deleteAllB2CCertification()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertB2CCertification(b2CCertification: B2CCertification):Long

}