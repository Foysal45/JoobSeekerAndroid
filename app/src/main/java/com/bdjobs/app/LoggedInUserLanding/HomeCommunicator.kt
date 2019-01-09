package com.bdjobs.app.LoggedInUserLanding

import com.bdjobs.app.API.ModelClasses.StatsModelClassData

interface HomeCommunicator {
    fun goToKeywordSuggestion()
    fun goToInterviewInvitation(from:String)
    fun goToEmployerViewedMyResume(from:String)
    fun goToFollowedEmployerList(from:String)
    fun goToFavSearchFilters()
    fun goToJoblistFromLastSearch()
    fun goToJobSearch(favID:String)
    fun shortListedClicked(Position:Int)
    fun backButtonClicked()
    fun getLastStatsData() : List<StatsModelClassData?>?
    fun getAllStatsData() : List<StatsModelClassData?>?
    fun goToAppliedJobs()
    fun setTime(time : String)

}