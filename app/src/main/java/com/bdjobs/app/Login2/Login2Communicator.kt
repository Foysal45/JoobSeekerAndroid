package com.bdjobs.app.Login2

interface Login2Communicator {
    fun backButtonClicked()
    fun goToPasswordFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?)
    fun goToOtpFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?)
    fun getFullName(): String?
    fun getUserId(): String?
    fun getImageUrl(): String?
    fun getUserName(): String?
    fun getGoToHome(): Boolean?
    fun goToHomePage()
//    fun goToRegistrationActivity()
//    fun goToWebActivity(url:String,from:String)
}