package com.bdjobs.app.Jobs

import com.bdjobs.app.API.ModelClasses.JobListModelData


@Suppress("SpellCheckingInspection")
interface JobCommunicator {
    fun showShortListIcon()
    fun hideShortListIcon()
    fun setTotalPage(pages:Int?)



    fun setCurrentJobPosition(from: Int)
    fun getCurrentJobPosition(): Int

    fun setBackFrom(from: String)
    fun getBackFrom(): String
    fun onItemClicked(position: Int)
    fun setJobList(jobList: MutableList<JobListModelData>?)
    fun getJobList(): MutableList<JobListModelData>?
    fun setPosition(position: Int)
    fun getItemClickPosition(): Int
    fun setpageNumber(pageNumber: Int)
    fun getCurrentPageNumber(): Int
    fun setTotalJob(totalPage: Int?)
    fun setLastPasge(lastPage: Boolean)
    fun setIsLoading(isLoading: Boolean)
    fun getTotalPage(): Int
    fun getLastPasge(): Boolean
    fun getIsLoading(): Boolean

    fun totalJobCount(totalJobFound: Int?)
    fun getTotalJobCount(): Int?
    fun goToLoginPage()
    fun backButtonPressesd()
    fun goToAdvanceSearch()

    fun goToSuggestiveSearch(from: String, typedData: String?)

    fun gotoJobList()

    fun goToAjkerDealLive(containerId:Int)


    // search params

    fun getFilterID():String?
    fun getFilterName():String?

    fun setKeyword(value: String?)
    fun setCategory(value: String?)
    fun setLocation(value: String?)
    fun setIndustry(value: String?)
    fun setNewsPaper(value: String?)

    fun getLocation(): String?
    fun getKeyword(): String?
    fun getCategory(): String?
    fun getNewsPaper(): String?
    fun getIndustry(): String?

    fun getOrganization(): String?
    fun getGender(): String?
    fun getExperience(): String?
    fun getJobType(): String?
    fun getJobLevel(): String?
    fun getJobNature(): String?
    fun getPostedWithin(): String?
    fun getDeadline(): String?
    fun getAge(): String?
    fun getArmy(): String?
    fun getWorkPlace(): String?
    fun getPersonWithDisability() : String?
    fun getFacilitiesForPWD() : String?


    fun setOrganization(value: String?)
    fun setGender(value: String?)
    fun setExperience(value: String?)
    fun setJobType(value: String?)
    fun setJobLevel(value: String?)
    fun setJobNature(value: String?)
    fun setPostedWithin(value: String?)
    fun setDeadline(value: String?)
    fun setAge(value: String?)
    fun setArmy(value: String?)
    fun setWorkPlace(value: String?)
    fun setPersonWithDisability(value: String?)
    fun setFacilitiesForPWD(value: String?)

    fun setFilterID(filterID: String?)
    fun setFilterName(filterName: String?)


    fun showShortListedIcon()
    fun showUnShortListedIcon()


    fun setTotalAppliedJobs(appliedJobsCount: Int)
    fun getTotalAppliedJobs() : Int

}