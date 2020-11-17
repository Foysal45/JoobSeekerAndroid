package com.bdjobs.app.databases.internal

import androidx.room.*
import java.util.*

@Dao
interface FollowedEmployerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFollowedEmployer(employer: FollowedEmployer)

    @Query("SELECT * FROM FollowedEmployer ORDER BY CompanyName")
    fun getAllFollowedEmployer(): List<FollowedEmployer>

    @Query("SELECT * FROM FollowedEmployer WHERE FollowedOn>=:dt ORDER BY CompanyName")
    fun getFollowedEmployerbyDate(dt: Date): List<FollowedEmployer>

    @Query("DELETE FROM FollowedEmployer")
    fun deleteAllFollowedEmployer()

    @Query("DELETE FROM FollowedEmployer WHERE CompanyID =:companyID AND CompanyName=:companyName")
    fun deleteFollowedEmployerByCompanyID(companyID: String, companyName: String)

    @Query("SELECT SUM(JobCount) FROM FollowedEmployer")
    fun getJobCountOfFollowedEmployer(): Int

    @Query("SELECT * FROM FollowedEmployer WHERE CompanyID=:companyID AND CompanyName=:companyName")
    fun getFollowedEmployer(companyID: String, companyName: String): List<FollowedEmployer>

    @Transaction
    fun isItFollowed(companyID: String, companyName: String): Boolean {
        val jobs = getFollowedEmployer(companyID, companyName)
        if (jobs.isNullOrEmpty()) {
            return false
        }
        return true
    }
}