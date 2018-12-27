package com.bdjobs.app.LoggedInUserLanding

interface HomeCommunicator {
    fun goToKeywordSuggestion()
    fun goToFollowedEmployerList()
    fun goToFavSearchFilters()
    fun goToJoblistFromLastSearch()
    fun goToJobSearch(favID:String)
    fun shortListedClicked(Position:Int)
}