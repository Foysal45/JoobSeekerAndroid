package com.bdjobs.app.FavouriteSearch

interface FavCommunicator {
    fun backButtonPressed()
    fun goToJobSearch(favID:String)
    fun goToEditMode(favID:String)
    fun getFilterID():String

}