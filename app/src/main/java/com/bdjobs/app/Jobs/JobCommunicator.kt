package com.bdjobs.app.Jobs



interface JobCommunicator {

    fun onItemClicked(position: Int)
    fun setJobList(jobList: MutableList<DataItem>?)
    fun getJobList(): MutableList<DataItem>?
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
    fun scrolledJobsNumber(position:Int)
    fun totalJobCount(totalJobFound:Int?)
    fun getTotalJobCount():Int?






}