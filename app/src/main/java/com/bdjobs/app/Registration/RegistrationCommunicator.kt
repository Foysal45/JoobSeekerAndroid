package com.bdjobs.app.Registration

import org.jetbrains.anko.toast

interface RegistrationCommunicator {

    //------------white collar-------------//
    fun gotToStepWhiteCollar()
    fun wcGoToStepSocialInfo()
    fun wcGoToStepName()
    fun wcGoToStepGender()
    fun wcGoToStepPhoneEmail()
    fun wcGoToStepPassword()
    fun wcGoToStepCongratulation()
    fun wcGoToStepMobileVerification()
    fun wcCategorySelected(category: String, position: Int)
    fun wcNameSelected(name : String)
    fun wcGenderSelected(gender: String)
    fun wcMobileNumberSelected(mobileNumber: String)
    fun wcEmailSelected(email : String)
    fun wcGetMobileNumber():String
    fun wcGetEmail():String
    fun wcUserNameTypeSelected(userId: String)
    fun wcSetPassAndConfirmPassword(password:String,confirmPass:String)
    fun wcCreateAccount()
    fun wcUserNameSelected(userName: String)
    fun wcCountrySeledted(countryCode: String)
    fun wcOtpVerify()
    fun wcSetOtp(otp: String)
    fun wcGetOtp():String


    //-------------blue collar------------//
    fun goToStepBlueCollar()
    fun bcGoToStepName()
    fun bcGoToStepGender()
    fun bcGoToStepMobileNumber()
    fun bcGoToStepOtpCode()
    fun bcGoToStepBirthDate()
    fun bcGoToStepAdress()
    fun bcGoToStepExperience()
    fun bcGoToStepEducation()
    fun bcGoToStepPhotoUpload()
    fun bcGoToStepCongratulation()
    /*fun setProgreesBar()*/



}