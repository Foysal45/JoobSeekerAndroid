package com.bdjobs.app.Login

interface LoginCommunicator {
    fun backButtonClicked()
    fun goToPasswordFragment(userName:String?,userId:String?,fullName:String?,imageUrl:String?)
    fun goToOtpFragment(userName:String?,userId:String?,fullName:String?,imageUrl:String?)
    fun getFullName():String?
    fun getUserId():String?
    fun getImageUrl():String?
    fun getUserName():String?
}