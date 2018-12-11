package com.bdjobs.app.Registration

import org.jetbrains.anko.toast

interface RegistrationCommunicator {

    //white collar

    fun gotToStepWhiteCollar()
    fun wcGoToStepSocialInfo()
    fun wcGoToStepName()
    fun wcGoToStepGender()
    fun wcGoToStepPhoneEmail()
    fun wcGoToStepPassword()
    fun wcGoToStepCongratulation()
    //blue collar
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
    fun setProgreesBar()



}