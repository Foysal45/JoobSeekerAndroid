package com.bdjobs.app.ManageResume

interface ManageResumeCommunicator {
    fun isGetCvUploaded() : String
    fun backButtonPressed()
    fun gotoupload()
    fun gotouploaddone()
    fun gotoTimesResumeFrag()
}