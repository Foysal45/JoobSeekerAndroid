package com.bdjobs.app.Databases.Internal

import androidx.room.*

@Dao
interface FollowedEmployerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFollowedEmployer(employer: FollowedEmployer)

    @Query("SELECT * FROM FollowedEmployer ORDER BY CompanyName")
    fun getAllFollowedEmployer(): List<FollowedEmployer>

    @Query("DELETE FROM FollowedEmployer")
    fun deleteAllFollowedEmployer()

    @Query("DELETE FROM FollowedEmployer WHERE CompanyID =:companyID")
    fun deleteFollowedEmployerByCompanyID(companyID:String)

}