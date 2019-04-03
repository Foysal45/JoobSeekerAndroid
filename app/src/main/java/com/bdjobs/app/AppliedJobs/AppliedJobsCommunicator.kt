package com.bdjobs.app.AppliedJobs

import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience

interface AppliedJobsCommunicator {
    fun backButtonPressed()
    fun gotoAppliedJobsFragment()
    fun getTime(): String
    fun scrollToUndoPosition(position:Int)
    fun decrementCounter()
    fun gotoInterviewInvitationDetails(from: String, jobID: String, companyName: String, jobTitle: String)
    fun gotoEmployerInteractionFragment()
    fun getjobID() : String
    fun setjobID(jobid : String)
    fun setexperienceList(AppliedJobModelExprience : ArrayList<AppliedJobModelExprience>)
    fun getExperience() : ArrayList<AppliedJobModelExprience>
    fun setComapany(company : String)
    fun getCompany(): String
    fun setTitle(title : String)
    fun getTitle2(): String
    fun setFrom(value : String)
    fun getFrom():String
    fun setStatus(status : String)
    fun getStatus():String

}