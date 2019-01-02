package com.bdjobs.app.LoggedInUserLanding

import com.bdjobs.app.API.ModelClasses.StatsModelClassData

interface HomeCommunicator {
    fun goToKeywordSuggestion()
    fun goToFollowedEmployerList()
    fun goToFavSearchFilters()
    fun goToJoblistFromLastSearch()
    fun goToJobSearch(favID:String)
    fun shortListedClicked(Position:Int)
    fun backButtonClicked()
    fun getLastStatsData() : List<StatsModelClassData?>?
    fun getAllStatsData() : List<StatsModelClassData?>?

}