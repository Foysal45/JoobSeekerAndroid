package com.bdjobs.app.AppliedJobs

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

}