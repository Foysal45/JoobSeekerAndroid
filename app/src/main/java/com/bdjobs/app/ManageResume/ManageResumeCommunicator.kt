package com.bdjobs.app.ManageResume

interface ManageResumeCommunicator {
    fun backButtonPressed()
    fun gotoResumeUploadFragment()
    fun gotoDownloadResumeFragment()
    fun gotoEmailResumeFragment()
    fun gotoTimesResumeFrag()
    fun getjobID() : String
    fun getEmailTo() : String
    fun getSubject() : String
}