package com.bdjobs.app.Registration

import android.app.Activity
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CreateAccountModel
import com.bdjobs.app.API.ModelClasses.DataLoginPasswordModel
import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob

import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Registration.blue_collar_registration.*
import com.bdjobs.app.Registration.white_collar_registration.*
import com.bdjobs.app.SessionManger.BdjobsUserSession

import com.bdjobs.app.Utilities.transitFragment
import kotlinx.android.synthetic.main.activity_registration_base.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationBaseActivity : Activity(), RegistrationCommunicator {



    //white Collar
    private val registrationLandingFragment = RegistrationLandingFragment()
    private val wccategoryFragment = WCCategoryFragment()
    private val wcSocialInfoFragment = WCSocialInfoFragment()
    private val wcNameFragment = WCNameFragment()
    private val wcGenderFragment = WCGenderFragment()
    private val wcPhoneEmailFragment = WCPhoneEmailFragment()
    private val wcPasswordFragment = WCPasswordFragment()
    private val wcCongratulationFragment = WCCongratulationFragment()
    private val wcMobileVerificationFragment = WCOtpCodeFragment()
    private lateinit var categoryId: String
    private lateinit var dataStorage: DataStorage
    private lateinit var name: String
    private lateinit var gender: String
    private var mobileNumber: String = ""
    private var wcEmail: String = ""
    private var userNameType: String = ""
    private var wcPassword: String = ""
    private var wcConfirmPass: String = ""
    private var userName: String = ""
    private var socialMediaId : String =""
    private var isSMediaLogin  = "False"
    private var socialMediaType : String = ""
    private var categoryType : String = ""
    private var wcCountryCode : String = ""
    private var tempId : String = ""
    private var otpCode : String = ""


    // blue Collar
    private val bcCategoryFragment = BCCategoryFragment()
    private val bcNameFragment = BCNameFragment()
    private val bcGenderFragment = BCGenderFragment()
    private val bcMobileNumberFragment = BCMobileNumberFragment()
    private val bcOtpCodeFragment = BCOtpCodeFragment()
    private val bcBirthDateFragment = BCBirthDateFragment()
    private val bcAdressFragment = BCAdressFragment()
    private val bcExperienceFragment = BCExperienceFragment()
    private val bcEducationFragment = BCEducationFragment()
    private val bcPhotoUploadFragment = BCPhotoUploadFragment()
    private val bcCongratulationFragment = BCCongratulationFragment()

    //-------------api response value----------//

    private var isCVPostedRPS = ""
    private var nameRPS = ""
    private var emailRPS = ""
    private var userID = ""
    private var decodeId = ""
    private var userNameRPS= ""
    private var appsDate = ""
    private var ageRPS = ""
    private var experienseRPS = ""
    private var categoryIDRPS = ""
    private var genderRPS = ""
    private var resumeUpdateOn = ""
    private var isResumeUpdate = ""
    private var trainingId = ""
    private var userPicUrl =""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        stepProgressBar.visibility = View.GONE

        dataStorage = DataStorage(this)

        transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)

        backIcon.setOnClickListener {

            onBackPressed()

            setProgreesBar()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        setProgreesBar()
    }

    override fun goToHomePage() {

        DatabaseUpdateJob.runJobImmediately()
        val intent = Intent(this@RegistrationBaseActivity, MainLandingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    //-----------------------  white Collar ------------------------//


    override fun gotToStepWhiteCollar() {
        categoryType = "0"
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 17
        transitFragment(wccategoryFragment, R.id.registrationFragmentHolderFL, true)

    }

    override fun wcGoToStepSocialInfo() {

        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 34
        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL, true)


    }


    override fun wcGoToStepName() {

        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 51
        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL, true)


    }

    override fun wcGoToStepGender() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 68
        transitFragment(wcGenderFragment, R.id.registrationFragmentHolderFL, true)


    }

    override fun wcGoToStepPhoneEmail() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 85
        transitFragment(wcPhoneEmailFragment, R.id.registrationFragmentHolderFL, true)


    }


    override fun wcGoToStepPassword() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100
        transitFragment(wcPasswordFragment, R.id.registrationFragmentHolderFL, true)


    }

    override fun wcGoToStepCongratulation() {
        stepProgressBar.visibility = View.INVISIBLE
        transitFragment(wcCongratulationFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun wcGoToStepMobileVerification() {
        stepProgressBar.visibility = View.INVISIBLE
        transitFragment(wcMobileVerificationFragment, R.id.registrationFragmentHolderFL, true)

    }

    override fun wcCategorySelected(category: String, position: Int) {

        wccategoryFragment.goToNextStep()
        categoryId = dataStorage.getCategoryIDByName(category)!!
        Log.d("catagorySelected", "catagory $category")
        Log.d("catagorySelected", "categoryId $categoryId")

    }


    override fun wcNameSelected(name: String) {

        this.name = name
        Log.d("catagorySelected", "name ${this.name}")
    }

    override fun wcGenderSelected(gender: String) {

        this.gender = gender

        Log.d("catagorySelected", "gender ${this.gender}")
        wcGenderFragment.goToNextStep()

    }

    override fun wcMobileNumberSelected(mobileNumber: String) {

        this.mobileNumber = mobileNumber
        Log.d("catagorySelected", "mobileNumber ${this.mobileNumber}")

    }

    override fun wcEmailSelected(email: String) {

        wcEmail = email
        Log.d("catagorySelected", "wcEmail $wcEmail")
    }

    override fun wcGetMobileNumber(): String {
        return mobileNumber
    }

    override fun wcGetEmail(): String {
        return wcEmail
    }

    override fun wcUserNameTypeSelected(userId: String) {

        userNameType = userId
        Log.d("catagorySelected", "userNameType test $userNameType")

    }

    override fun wcSetPassAndConfirmPassword(password: String, confirmPass: String) {


        wcPassword = password
        wcConfirmPass = confirmPass

        Log.d("catagorySelected", "wcPassword $wcPassword , wcConfirmPass $wcConfirmPass ")

    }

    override fun wcUserNameSelected( userName :String) {

        this.userName = userName
        Log.d("catagorySelected", "userName first ${this.userName} ")
    }


    override fun wcCountrySeledted(countryCode: String) {
        wcCountryCode = countryCode
        Log.d("catagorySelected", "wcCountryCode  ${this.wcCountryCode} ")
    }


    override fun wcSetOtp(otp: String) {

        this.otpCode = otp
        Log.d("catagorySelected", "otpCode  ${this.otpCode} ")

    }

    override fun wcGetOtp(): String {
        Log.d("catagorySelected", "otpCode  $otpCode ")
        return otpCode

    }

    override fun wcCreateAccount() {

        var firstName = name
        var lastName = ""
        val splitedName = name.trim({ it <= ' ' }).split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (splitedName.size > 0) {
            firstName = splitedName[0]
            for (i in 1 until splitedName.size) {
                lastName = lastName + splitedName[i] + " "
            }
            lastName = lastName.trim { it <= ' ' }
        }

        Log.d("ResponseTesrt",
                "Name: " + name + "\n" +
                        "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName + "\n" +
                        "CategoryID: " + categoryId + "\n" +
                        "Gender: " + gender + "\n" +
                        "CountryCode: " + wcCountryCode + "\n" +
                        "Mobile: " + mobileNumber + "\n" +
                        "userName vbcvb " + userName + "\n" +
                        "password: " + wcPassword + "\n" +
                        "confirmPassword " + wcConfirmPass + "\n" +
                        "Mobile: " + mobileNumber + "\n" +
                        "categoryType " + categoryType + "\n" +
                        "userNameType dfdf " + userNameType + "\n" +
                        "countryCode " + "" + "\n" +
                        "email " + wcEmail + "\n" +
                        "sMediatype " + socialMediaType + "\n" +
                        "isSMLogin " + isSMediaLogin + "\n" +
                        "sMid " + "" + "\n"
        )

        ApiServiceMyBdjobs.create().createAccount(firstName,  lastName, gender, wcEmail, userName, wcPassword, wcConfirmPass, mobileNumber, socialMediaId, isSMediaLogin,categoryType, userNameType, socialMediaType,categoryId, "88" ,"","").enqueue(object : Callback<CreateAccountModel> {
            override fun onFailure(call: Call<CreateAccountModel>, t: Throwable) {
                Log.d("ResponseTesrt", " onFailure ${t.message}")
            }

            override fun onResponse(call: Call<CreateAccountModel>, response: Response<CreateAccountModel>) {

                Log.d("ResponseTesrt", " onResponse message ${response.body()!!.message}")
                Log.d("ResponseTesrt", " onResponse statuscode ${response.body()!!.statuscode}")

                if (response.isSuccessful){
                    if(response.body()!!.statuscode.equals("0",true)){

                        val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                        Log.d("ResponseTesrt", " isCvPosted ${isCvPosted}")
                        if (isCvPosted.equals("null",true)){

                            tempId = response.body()!!.data!!.get(0)!!.tmpId!!

                            Log.d("ResponseTesrt", " tempId $tempId")
                            Log.d("ResponseTesrt", " in first Condition")
                            wcGoToStepMobileVerification()

                        } else {

                            wcGoToStepCongratulation()
                            val bdjobsUserSession = BdjobsUserSession(this@RegistrationBaseActivity)



                            isCVPostedRPS = response.body()!!.data!!.get(0)!!.isCvPosted.toString()
                            nameRPS = response.body()!!.data!!.get(0)!!.name.toString()
                            emailRPS = response.body()!!.data!!.get(0)!!.email.toString()
                            userID = response.body()!!.data!!.get(0)!!.userId.toString()
                            decodeId = response.body()!!.data!!.get(0)!!.decodId.toString()
                            userNameRPS = response.body()!!.data!!.get(0)!!.userName.toString()
                            appsDate = response.body()!!.data!!.get(0)!!.appsDate.toString()
                            ageRPS = response.body()!!.data!!.get(0)!!.age.toString()
                            experienseRPS = response.body()!!.data!!.get(0)!!.exp.toString()
                            categoryIDRPS = response.body()!!.data!!.get(0)!!.catagoryId.toString()
                            genderRPS = response.body()!!.data!!.get(0)!!.gender.toString()
                            resumeUpdateOn = response.body()!!.data!!.get(0)!!.resumeUpdateON.toString()
                            isResumeUpdate = response.body()!!.data!!.get(0)!!.isResumeUpdate.toString()
                            trainingId = response.body()!!.data!!.get(0)!!.trainingId.toString()
                            userPicUrl = response.body()!!.data!!.get(0)!!.userPicUrl.toString()



                            bdjobsUserSession.createSession(isCVPostedRPS,nameRPS,emailRPS,userID,decodeId,
                                    userNameRPS,appsDate,ageRPS,experienseRPS,categoryIDRPS,genderRPS,
                                    resumeUpdateOn,isResumeUpdate,trainingId,userPicUrl)
                        }

                    } else if (response.body()!!.statuscode.equals("2",true)){

                        toast(response.body()!!.message!!)
                    }







                }

            }

        })


    }


    override fun wcOtpVerify() {


        ApiServiceMyBdjobs.create().sendOtpToVerify(tempId,otpCode).enqueue(object : Callback<CreateAccountModel> {
            override fun onFailure(call: Call<CreateAccountModel>, t: Throwable) {
                Log.d("ResponseTesrt", " wcOtpVerify onFailure ${t.message}")
            }

            override fun onResponse(call: Call<CreateAccountModel>, response: Response<CreateAccountModel>) {

                Log.d("ResponseTesrt", " wcOtpVerify onResponse message ${response.body()!!.message}")
                Log.d("ResponseTesrt", " wcOtpVerify onResponse statuscode ${response.body()!!.statuscode}")

                val username = response.body()!!.data!!.get(0)!!.userName.toString()

                Log.d("ResponseTesrt", " wcOtpVerify name ${username}")
                if (response.isSuccessful){
                    if(response.body()!!.statuscode.equals("0",true)){

                        val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                        Log.d("ResponseTesrt", " wcOtpVerify isCvPosted ${isCvPosted}")
                        if (!isCvPosted.equals("null",true)){

                            wcGoToStepCongratulation()
                            val bdjobsUserSession = BdjobsUserSession(this@RegistrationBaseActivity)



                            isCVPostedRPS = response.body()!!.data!!.get(0)!!.isCvPosted.toString()
                            nameRPS = response.body()!!.data!!.get(0)!!.name.toString()
                            emailRPS = response.body()!!.data!!.get(0)!!.email.toString()
                            userID = response.body()!!.data!!.get(0)!!.userId.toString()
                            decodeId = response.body()!!.data!!.get(0)!!.decodId.toString()
                            userNameRPS = response.body()!!.data!!.get(0)!!.userName.toString()
                            appsDate = response.body()!!.data!!.get(0)!!.appsDate.toString()
                            ageRPS = response.body()!!.data!!.get(0)!!.age.toString()
                            experienseRPS = response.body()!!.data!!.get(0)!!.exp.toString()
                            categoryIDRPS = response.body()!!.data!!.get(0)!!.catagoryId.toString()
                            genderRPS = response.body()!!.data!!.get(0)!!.gender.toString()
                            resumeUpdateOn = response.body()!!.data!!.get(0)!!.resumeUpdateON.toString()
                            isResumeUpdate = response.body()!!.data!!.get(0)!!.isResumeUpdate.toString()
                            trainingId = response.body()!!.data!!.get(0)!!.trainingId.toString()
                            userPicUrl = response.body()!!.data!!.get(0)!!.userPicUrl.toString()



                            bdjobsUserSession.createSession(isCVPostedRPS,nameRPS,emailRPS,userID,decodeId,
                                    userNameRPS,appsDate,ageRPS,experienseRPS,categoryIDRPS,genderRPS,
                                    resumeUpdateOn,isResumeUpdate,trainingId,userPicUrl)




                        }

                    } else if (response.body()!!.statuscode.equals("3",true)){

                        toast(response.body()!!.message!!)
                    }

                    else if (response.body()!!.statuscode.equals("1",true)){

                        toast(response.body()!!.message!!)
                    }







                }

            }

        })



    }


    // -----------------------------  blue Collar start ------------------  //
    override fun goToStepBlueCollar() {
        categoryType = "1"
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 10
        transitFragment(bcCategoryFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepName() {

        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 20
        transitFragment(bcNameFragment, R.id.registrationFragmentHolderFL, true)

    }

    override fun bcGoToStepGender() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 30
        transitFragment(bcGenderFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepMobileNumber() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 40
        transitFragment(bcMobileNumberFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepOtpCode() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 50
        transitFragment(bcOtpCodeFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepBirthDate() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 60
        transitFragment(bcBirthDateFragment, R.id.registrationFragmentHolderFL, true)
    }


    override fun bcGoToStepAdress() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 70
        transitFragment(bcAdressFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepExperience() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 80
        transitFragment(bcExperienceFragment, R.id.registrationFragmentHolderFL, true)
    }

    override fun bcGoToStepEducation() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 90
        transitFragment(bcEducationFragment, R.id.registrationFragmentHolderFL, true)
    }


    override fun bcGoToStepPhotoUpload() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100
        transitFragment(bcPhotoUploadFragment, R.id.registrationFragmentHolderFL, true)
    }


    override fun bcGoToStepCongratulation() {
        stepProgressBar.visibility = View.GONE
        transitFragment(bcCongratulationFragment, R.id.registrationFragmentHolderFL, true)


    }


    override fun onBackPressed() {
        super.onBackPressed()
        setProgreesBar()
    }

    private fun setProgreesBar() {
        val currentFragment = fragmentManager.findFragmentById(R.id.registrationFragmentHolderFL)
        Log.d("stepChange", "FragmentLive: $currentFragment")

        when (currentFragment) {

            registrationLandingFragment -> {

                Log.d("stepChange", " in registrationLandingFragment")
                stepProgressBar.visibility = View.GONE

            }
            wccategoryFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 17
                Log.d("stepChange", " in wccategoryFragment")
            }

            wcSocialInfoFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 34
                Log.d("stepChange", " in wccategoryFragment")
            }
            wcNameFragment -> {

                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 51
                Log.d("stepChange", " in wccategoryFragment")
            }
            wcGenderFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 68
                Log.d("stepChange", " in wccategoryFragment")
            }
            wcPhoneEmailFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 85
                Log.d("stepChange", " in wccategoryFragment")
            }
            wcPasswordFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 100
                Log.d("stepChange", " in wccategoryFragment")
            }

            // blue collar

            bcNameFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 20
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcGenderFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 30
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcMobileNumberFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 40
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcOtpCodeFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 50
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcBirthDateFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 60
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcAdressFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 70
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcExperienceFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 80
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcEducationFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 90
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcPhotoUploadFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 100
                Log.d("stepChange", " in wccategoryFragment")
            }

            bcCategoryFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 10
                Log.d("stepChange", " in wccategoryFragment")
            }


        }

    }

}
