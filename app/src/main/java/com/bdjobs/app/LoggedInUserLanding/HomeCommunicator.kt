package com.bdjobs.app.LoggedInUserLanding

interface HomeCommunicator {
    fun goToKeywordSuggestion()
    fun goToInterviewInvitation(from:String)
    fun goToFollowedEmployerList()
    fun goToFavSearchFilters()
    fun goToJoblistFromLastSearch()
    fun goToJobSearch(favID:String)
    fun shortListedClicked(Position:Int)
    fun backButtonClicked()
}