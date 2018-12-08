package com.bdjobs.app.Databases.Internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "Suggestion", indices = [(Index(value = ["Suggestions"], unique = true))])
data class Suggestion(@ColumnInfo(name = "Suggestions")
                      val suggestion: String,
                      @ColumnInfo(name = "Flag")
                      val flag: String,
                      @ColumnInfo(name = "UserID")
                      val userID: String? = null,
                      @ColumnInfo(name = "InsertTime")
                      val time: Date) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "FollowedEmployer", indices = [(Index(value = ["CompanyID"], unique = true))])
data class FollowedEmployer(@ColumnInfo(name = "CompanyID")
                            val CompanyID: String?,
                            @ColumnInfo(name = "CompanyName")
                            val CompanyName: String?,
                            @ColumnInfo(name = "FollowedOn")
                            val FollowedOn: Date?,
                            @ColumnInfo(name = "JobCount")
                            val JobCount: String? = null) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "AppliedJobs", indices = [(Index(value = ["appliedid"], unique = true))])
data class AppliedJobs(@ColumnInfo(name = "appliedid")
                       val appliedid: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "ShortListedJobs", indices = [(Index(value = ["jobid"], unique = true))])
data class ShortListedJobs(@ColumnInfo(name = "jobid")
                           val jobid: String?,
                           @ColumnInfo(name = "jobtitle")
                           val jobtitle: String?,
                           @ColumnInfo(name = "companyname")
                           val companyname: String?,
                           @ColumnInfo(name = "deadline")
                           val deadline: Date?,
                           @ColumnInfo(name = "eduRec")
                           val eduRec: String?,
                           @ColumnInfo(name = "experience")
                           val experience: String? = null,
                           @ColumnInfo(name = "standout")
                           val standout: String?,
                           @ColumnInfo(name = "logo")
                           val logo: String?,
                           @ColumnInfo(name = "lantype")
                           val lantype: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "FavouriteSearch", indices = [(Index(value = ["filterid"], unique = true))])
data class FavouriteSearch(
        @ColumnInfo(name = "filterid")
        val filterid: String?,
        @ColumnInfo(name = "filtername")
        val filtername: String?,
        @ColumnInfo(name = "industrialCat")
        val industrialCat: String?,
        @ColumnInfo(name = "functionalCat")
        val functionalCat: String?,
        @ColumnInfo(name = "location")
        val location: String?,
        @ColumnInfo(name = "organization")
        val organization: String?,
        @ColumnInfo(name = "jobnature")
        val jobnature: String?,
        @ColumnInfo(name = "joblevel")
        val joblevel: String?,
        @ColumnInfo(name = "postedon")
        val postedon: String?,
        @ColumnInfo(name = "deadline")
        val deadline: String?,
        @ColumnInfo(name = "keyword")
        val keyword: String?,
        @ColumnInfo(name = "newspaper")
        val newspaper: String?,
        @ColumnInfo(name = "gender")
        val gender: String?,
        @ColumnInfo(name = "genderb")
        val genderb: String?,
        @ColumnInfo(name = "experience")
        val experience: String?,
        @ColumnInfo(name = "age")
        val age: String?,
        @ColumnInfo(name = "jobtype")
        val jobtype: String?,
        @ColumnInfo(name = "retiredarmy")
        val retiredarmy: String?,
        @ColumnInfo(name = "createdon")
        val createdon: Date?,
        @ColumnInfo(name = "updatedon")
        val updatedon: Date?,
        @ColumnInfo(name = "totaljobs")
        val totaljobs: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}


@Entity(tableName = "JobInvitation", indices = [(Index(value = ["jobId"], unique = true))])
data class JobInvitation(@ColumnInfo(name = "companyName")
                         val companyName: String?,
                         @ColumnInfo(name = "inviteDate")
                         val inviteDate: String?,
                         @ColumnInfo(name = "jobId")
                         val jobId: String? = null,
                         @ColumnInfo(name = "jobTitle")
                         val jobTitle: String? = null,
                         @ColumnInfo(name = "seen")
                         val seen: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}


@Entity(tableName = "B2CCertification", indices = [(Index(value = ["aid"], unique = true))])
data class B2CCertification(@ColumnInfo(name = "jobRole")
                            val jobRole: String?,
                            @ColumnInfo(name = "testDate")
                            val testDate: String?,
                            @ColumnInfo(name = "sid")
                            val sid: String? = null,
                            @ColumnInfo(name = "jid")
                            val jid: String? = null,
                            @ColumnInfo(name = "jrid")
                            val jrid: String? = null,
                            @ColumnInfo(name = "aid")
                            val aid: String?=null,
                            @ColumnInfo(name = "sType")
                            val sType: String? = null,
                            @ColumnInfo(name = "ajid")
                            val ajid: String? = null,
                            @ColumnInfo(name = "res")
                            val res: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}