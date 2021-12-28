package com.bdjobs.app.manageResume

interface ManageResumeCommunicator {
    fun backButtonPressed()
    fun gotoResumeUploadFragment()
    fun gotoDownloadResumeFragment()
    fun gotoEmailResumeFragment()
    fun gotoTimesResumeFrag()
    fun gotoTimesResumeFilterFrag()
    fun getjobID() : String
    fun getEmailTo() : String
    fun getSubject() : String
    fun setBackFrom(string: String)
    fun getBackFrom() : String
}