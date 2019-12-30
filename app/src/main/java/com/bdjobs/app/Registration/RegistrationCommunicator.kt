package com.bdjobs.app.Registration

import com.bdjobs.app.API.ModelClasses.AddExpModel

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
    fun wcGenderSelected(gender: String)
    fun wcMobileNumberSelected(mobileNumber: String)
    fun wcEmailSelected(email: String)
    fun wcGetMobileNumber(): String
    fun wcGetEmail(): String
    fun wcUserNameTypeSelected(userId: String)
    fun wcSetPassAndConfirmPassword(password: String, confirmPass: String)

    fun wcCreateAccount()
    fun wcUserNameSelected(userName: String)
    fun wcCountrySeledted(countryCode: String)
    fun wcOtpVerify()
    fun wcSetOtp(otp: String)
    fun wcGetOtp(): String


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
    fun bcGoToStepPhotoUpload(hasEducation: String)
    fun bcGoToStepCongratulation()
    fun bcCategorySelected(category: String, position: Int)
    fun bcGenderSelected(gender: String)
    fun bcBirthDateAndAgeSelected(birthDate: String, age: String)
    fun bcSelectedBlueCollarSubCategoriesIDandExperince(IDs: String, experience: String, skilledBy: String, ntvqfLevel: String, categoryID: String, category: String)
    fun bcAddressSelected(district: String, thana: String, postOffice: String, address: String, locationID: String)
    fun bcGetAge(): String
    fun bcResendOtp()
    fun bcEducationSelected(eduLevel: String, eduDegree: String, instName: String, passingYear: String, educationType: String, board: String)

    ////-------------common-----------//
    fun nameSelected(name: String)

    fun goToHomePage()
    fun getUserId(): String
    fun getDecodeId(): String
    fun regWithGoogle()
    fun regWithFacebook()
    fun regWithLinkedIn()
    fun getName(): String
    fun getEmail(): String
    fun getGender(): String
    fun showProgressBar()
    fun hideProgressBar()
    fun getCategory(): String
    fun clearData()

    fun checkInviteCodeEligibility()


    fun getINLROData(): String


    fun setItemClick(position: Int)
    fun getItemClick(): Int
    fun showEditDialog(item: AddExpModel)

    fun getCategoryId(): String

}