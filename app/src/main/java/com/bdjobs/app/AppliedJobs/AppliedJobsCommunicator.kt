package com.bdjobs.app.AppliedJobs

interface AppliedJobsCommunicator {
    fun backButtonPressed()
    fun gotoAppliedJobsFragment()
    fun getTime(): String
    fun scrollToUndoPosition(position:Int)
    fun decrementCounter()

}