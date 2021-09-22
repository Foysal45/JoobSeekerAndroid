package com.bdjobs.app.LoggedInUserLanding

import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import java.util.*

interface HomeCommunicator {
    fun goToKeywordSuggestion()
    fun goToInterviewInvitation(from:String)
    fun goToVideoInvitation(from:String)
    fun goToLiveInvitation(from:String)
    fun goToEmployerViewedMyResume(from:String)
    fun goToFollowedEmployerList(from:String)
    fun goToFavSearchFilters()
    fun goToJoblistFromLastSearch()
    fun goToJobSearch(favID:String)
    fun shortListedClicked(jobids: ArrayList<String>, lns: ArrayList<String>, deadline: ArrayList<String>)
    fun backButtonClicked()
    fun getLastStatsData() : List<StatsModelClassData?>?
    fun getAllStatsData() : List<StatsModelClassData?>?
    fun goToAppliedJobs()
    fun setTime(time : String)
    fun scrollToUndoPosition(position:Int)
    fun decrementCounter()
    fun getInviteCodeUserType():String?
    fun getInviteCodepcOwnerID():String?
    fun getInviteCodeStatus():String?
    fun isGetCvUploaded() : String
    fun goToShortListedFragment(deadline:Int)
    fun setShortListFilter(filter:String)
    fun getShortListFilter():String
    fun gotoTimesEmailedResume(times_last : Boolean)
    fun gotoJobSearch()
    fun gotoAllJobSearch()
    fun gotoEditresume()
    fun showManageResumePopup()
    fun goToMessageByEmployers(value: String)
    fun goToNotifications()
    fun goToMessages()
    fun goToResumeManager()

    fun getTotalJobCounter():Int
    fun setTotalJobCounter(count:Int)

    // followed employers
    fun gotoJobListFragment(companyID : String?, companyName : String?)
    fun positionClicked(position:Int?)
    fun getPositionClicked():Int?
    fun decrementCounterFollowedEmp(position:Int)
    fun setFollowedEmployerList( empList:ArrayList<FollowEmployerListData>?)
    fun getFollowedEmployerList():ArrayList<FollowEmployerListData>?
    fun getCompanyID() : String
    fun getCompanyName() : String
}