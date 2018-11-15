package com.bdjobs.app.Login

import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListData

interface LoginCommunicator {
    fun backButtonClicked()
    fun goToPasswordFragment(userName:String?,userId:String?,fullName:String?,imageUrl:String?)
    fun goToOtpFragment(userName:String?,userId:String?,fullName:String?,imageUrl:String?)
    fun goToSocialAccountListFragment(socialLoginAccountDataList: List<SocialLoginAccountListData?>?)

    fun getSocialLoginAccountDataList(): List<SocialLoginAccountListData?>?

    fun getFullName():String?
    fun getUserId():String?
    fun getImageUrl():String?
    fun getUserName():String?
}