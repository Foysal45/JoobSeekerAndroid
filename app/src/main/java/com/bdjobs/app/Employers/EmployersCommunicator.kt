package com.bdjobs.app.Employers

interface EmployersCommunicator {
    fun backButtonPressed()
    fun gotoJobListFragment(companyID : String?, companyName : String?)
    fun getCompanyID() : String
    fun getCompanyName() : String
    fun getJobId() : String
    fun getTime() : String

}