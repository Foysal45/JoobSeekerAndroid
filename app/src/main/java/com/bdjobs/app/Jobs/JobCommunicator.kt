package com.bdjobs.app.Jobs

import com.bdjobs.app.API.ModelClasses.JobListModelData


interface JobCommunicator {

    fun onItemClicked(position: Int)
    fun setJobList(jobList: MutableList<JobListModelData>?)
    fun getJobList(): MutableList<JobListModelData>?
    fun setPosition(position : Int)
    fun getItemClickPosition() : Int
    fun setpageNumber(pageNumber : Int)
    fun getCurrentPageNumber(): Int
    fun setTotalJob(totalPage : Int)
    fun setLastPasge(lastPage : Boolean)
    fun setIsLoading(isLoading : Boolean)
    fun getTotalPage() : Int
    fun getLastPasge(): Boolean
    fun getIsLoading() : Boolean

    fun totalJobCount(totalJobFound:Int?)
    fun getTotalJobCount():Int?
    fun goToLoginPage()
    fun backButtonPressesd()
    fun goToAdvanceSearch()

    fun goToSuggestiveSearch(from:String,typedData:String)


    // search params

    fun setLocation(location : String)
    fun getLocation():String

    fun setKeyword(keyword : String)
    fun getKeyword(): String

    fun getCategory():String
    fun setCategory(category : String)






}