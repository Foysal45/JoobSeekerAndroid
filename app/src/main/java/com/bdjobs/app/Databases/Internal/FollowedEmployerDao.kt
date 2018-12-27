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

    @Query("SELECT SUM(JobCount) FROM FollowedEmployer")
    fun getJobCountOfFollowedEmployer(): Int

    @Query("SELECT * FROM FollowedEmployer WHERE CompanyID=:companyID")
    fun getFollowedEmployerByCompanyID(companyID:String): List<FollowedEmployer>

    @Transaction
    fun isItFollowed(companyID:String): Boolean {
        val jobs = getFollowedEmployerByCompanyID(companyID)
        if (jobs.isNullOrEmpty()) {
            return false
        }
        return true
    }

}