package com.bdjobs.app.Databases.Internal

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "Suggestion", indices = [(Index(value = ["Suggestions"], unique = true))])
@Keep
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

@Entity(tableName = "FollowedEmployer", indices = [(Index(value = ["CompanyName"], unique = true))])
@Keep
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
@Keep
data class AppliedJobs(@ColumnInfo(name = "appliedid")
                       val appliedid: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "ShortListedJobs", indices = [(Index(value = ["jobid"], unique = true))])
@Keep
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
@Keep
data class FavouriteSearch(
        @ColumnInfo(name = "filterid")
        val filterid: String? = "",
        @ColumnInfo(name = "filtername")
        val filtername: String? = "",
        @ColumnInfo(name = "industrialCat")
        val industrialCat: String? = "",
        @ColumnInfo(name = "functionalCat")
        val functionalCat: String? = "",
        @ColumnInfo(name = "location")
        val location: String? = "",
        @ColumnInfo(name = "organization")
        val organization: String? = "",
        @ColumnInfo(name = "jobnature")
        val jobnature: String? = "",
        @ColumnInfo(name = "joblevel")
        val joblevel: String? = "",
        @ColumnInfo(name = "postedon")
        val postedon: String? = "",
        @ColumnInfo(name = "deadline")
        val deadline: String? = "",
        @ColumnInfo(name = "keyword")
        val keyword: String? = "",
        @ColumnInfo(name = "newspaper")
        val newspaper: String? = "",
        @ColumnInfo(name = "gender")
        val gender: String? = "",
        @ColumnInfo(name = "genderb")
        val genderb: String? = "",
        @ColumnInfo(name = "experience")
        val experience: String? = "",
        @ColumnInfo(name = "age")
        val age: String? = "",
        @ColumnInfo(name = "jobtype")
        val jobtype: String? = "",
        @ColumnInfo(name = "retiredarmy")
        val retiredarmy: String? = "",
        @ColumnInfo(name = "createdon")
        val createdon: Date?,
        @ColumnInfo(name = "updatedon")
        val updatedon: Date?,
        @ColumnInfo(name = "totaljobs")
        val totaljobs: String? = "",
        @ColumnInfo(name = "isSubscribed")
        var isSubscribed: String? = "",
        @ColumnInfo(name = "workPlace")
        val workPlace: String? = "",
        @ColumnInfo(name = "personWithDisability")
        val personWithDisability : String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}


@Entity(tableName = "JobInvitation", indices = [(Index(value = ["jobId"], unique = true))])
@Keep
data class JobInvitation(@ColumnInfo(name = "companyName")
                         val companyName: String?,
                         @ColumnInfo(name = "inviteDate")
                         val inviteDate: Date?,
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

@Entity(tableName = "VideoInvitation", indices = [(Index(value = ["jobId"], unique = true))])
@Keep
data class VideoInvitation(@ColumnInfo(name = "companyName")
                           val companyName: String?,
                           @ColumnInfo(name = "jobTitle")
                           val jobTitle: String? = null,
                           @ColumnInfo(name = "jobId")
                           val jobId: String? = null,
                           @ColumnInfo(name = "videoStatusCode")
                           val videoStatusCode: String? = null,
                           @ColumnInfo(name = "videoStatus")
                           val videoStatus: String? = null,
                           @ColumnInfo(name = "userSeenInterview")
                           val userSeenInterview: String? = null,
                           @ColumnInfo(name = "employerSeenDate")
                           val employerSeenDate: Date?,
                           @ColumnInfo(name = "dateStringForSubmission")
                           val dateStringForSubmission: Date?,
                           @ColumnInfo(name = "dateStringForInvitaion")
                           val dateStringForInvitaion: Date?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "LiveInvitation", indices = [(Index(value = ["jobId"], unique = true))])
@Keep
data class LiveInvitation(@ColumnInfo(name = "companyName")
                          val companyName: String?,
                          @ColumnInfo(name = "jobTitle")
                          val jobTitle: String? = null,
                          @ColumnInfo(name = "jobId")
                          val jobId: String? = null,
                          @ColumnInfo(name = "liveInterviewStatusCode")
                          val liveInterviewStatusCode: String? = null,
                          @ColumnInfo(name = "liveInterviewStatus")
                          val liveInterviewStatus: String? = null,
                          @ColumnInfo(name = "userSeenLiveInterview")
                          val userSeenLiveInterview: String?,
                          @ColumnInfo(name = "liveInterviewDate")
                          val liveInterviewDate: Date?,
                          @ColumnInfo(name = "liveInterviewTime")
                          val liveInterviewTime: String?,
                          @ColumnInfo(name = "dateStringForInvitaion")
                          val dateStringForInvitaion: Date?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "B2CCertification", indices = [(Index(value = ["aid"], unique = true))])
@Keep
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
                            val aid: String? = null,
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


@Entity(tableName = "LastSearch", indices = [(Index(value = ["searchTime"], unique = true))])
@Keep
data class LastSearch(
        @ColumnInfo(name = "searchTime")
        val searchTime: Date?,
        @ColumnInfo(name = "jobLevel")
        val jobLevel: String? = "",
        @ColumnInfo(name = "newsPaper")
        val newsPaper: String? = "",
        @ColumnInfo(name = "armyp")
        val armyp: String? = "",
        @ColumnInfo(name = "blueColur")
        val blueColur: String? = "",
        @ColumnInfo(name = "category")
        val category: String? = "",
        @ColumnInfo(name = "deadline")
        val deadline: String? = "",
        @ColumnInfo(name = "encoded")
        val encoded: String? = "",
        @ColumnInfo(name = "experince")
        val experince: String? = "",
        @ColumnInfo(name = "gender")
        val gender: String? = "",
        @ColumnInfo(name = "genderB")
        val genderB: String? = "",
        @ColumnInfo(name = "testDate")
        val testDate: String? = "",
        @ColumnInfo(name = "industry")
        val industry: String? = "",
        @ColumnInfo(name = "isFirstRequest")
        val isFirstRequest: String? = "",
        @ColumnInfo(name = "jobnature")
        val jobnature: String? = "",
        @ColumnInfo(name = "jobType")
        val jobType: String? = "",
        @ColumnInfo(name = "keyword")
        val keyword: String? = "",
        @ColumnInfo(name = "lastJPD")
        val lastJPD: String? = "",
        @ColumnInfo(name = "location")
        val location: String? = "",
        @ColumnInfo(name = "organization")
        val organization: String? = "",
        @ColumnInfo(name = "pageId")
        val pageId: String? = "",
        @ColumnInfo(name = "pageNumber")
        val pageNumber: Int? = 1,
        @ColumnInfo(name = "postedWithIn")
        val postedWithIn: String? = "",
        @ColumnInfo(name = "age")
        val age: String? = "",
        @ColumnInfo(name = "rpp")
        val rpp: String? = "",
        @ColumnInfo(name = "slno")
        val slno: String? = "",
        @ColumnInfo(name = "version")
        val version: String? = "",
        @ColumnInfo(name = "workPlace")
        val workPlace: String? = "",
        @ColumnInfo(name = "personWithDisability")
        val personWithDisability : String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}


@Entity(tableName = "InviteCodeInfo", indices = [(Index(value = ["userId"], unique = true))])
@Keep
data class InviteCodeInfo(@ColumnInfo(name = "userId")
                          val userId: String?,
                          @ColumnInfo(name = "userType")
                          val userType: String?,
                          @ColumnInfo(name = "pcOwnerID")
                          val pcOwnerID: String? = "",
                          @ColumnInfo(name = "inviteCodeStatus")
                          val inviteCodeStatus: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "Notification")
@Keep
data class Notification(@ColumnInfo(name = "type")
                        val type: String? = "",
                        @ColumnInfo(name = "server_id")
                        val serverId: String? = "",
                        @ColumnInfo(name = "seen")
                        val seen: Boolean? = false,
                        @ColumnInfo(name = "img_link")
                        val imageLink: String? = "",
                        @ColumnInfo(name = "link")
                        val link: String? = "",
                        @ColumnInfo(name = "is_deleted")
                        val isDeleted: Boolean? = false,
                        @ColumnInfo(name = "arrival_time")
                        val arrivalTime: Date? = null,
                        @ColumnInfo(name = "seen_time")
                        val seenTime: Date? = null,
                        @ColumnInfo(name = "payload")
                        val payload: String? = "",
                        @ColumnInfo(name = "job_title")
                        val jobTitle: String? = "",
                        @ColumnInfo(name = "title")
                        val title: String? = "",
                        @ColumnInfo(name = "body")
                        val body: String? = "",
                        @ColumnInfo(name = "company_name")
                        val companyName: String? = "",
                        @ColumnInfo(name = "notification_id")
                        val notificationId: String? = "",
                        @ColumnInfo(name = "lan_type")
                        val lanType: String? = "",
                        @ColumnInfo(name = "deadline")
                        val deadline: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
