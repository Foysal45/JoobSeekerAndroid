package com.bdjobs.app.Registration

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.*
//import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.InviteCodeInfo
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Registration.blue_collar_registration.*
import com.bdjobs.app.Registration.white_collar_registration.*
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Workmanager.DatabaseUpdateWorker
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login_base.*
import kotlinx.android.synthetic.main.activity_registration_base.*
import kotlinx.android.synthetic.main.fragment_bc_mobile_number.*
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*
import kotlinx.android.synthetic.main.fragment_wc_otp_code.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class RegistrationBaseActivity : Activity(), RegistrationCommunicator, ConnectivityReceiver.ConnectivityReceiverListener {

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null


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
    private var categoryId: String = ""
    private var category: String = ""
    private lateinit var dataStorage: DataStorage
    private var name: String = ""
    private var gender: String = ""
    private var mobileNumber: String = ""

    private var wcEmail: String = ""
    private var userNameType: String = ""
    private var wcPassword: String = ""
    private var wcConfirmPass: String = ""
    private var userName: String = ""
    private var socialMediaId: String = ""
    private var isSMediaLogin = "False"
    private var socialMediaType: String = ""
    private var categoryType: String = ""
    private var wcCountryCode: String = ""
    private var tempId: String = ""
    private var otpCode: String = ""


    // blue Collar
    private val bcCategoryFragment = BCCategoryFragment()
    private val bcNameFragment = BCNameFragment()
    private val bcGenderFragment = BCGenderFragment()
    private val bcMobileNumberFragment = BCMobileNumberFragment()
    private val bcOtpCodeFragment = BCOtpCodeFragment()
    private val bcBirthDateFragment = BCBirthDateFragment()
    private val bcAdressFragment = BCAddressFragment()
    private val bcExperienceFragment = BCExperienceFragment()
    private val bcEducationFragment = BCEducationFragment()
    private val bcPhotoUploadFragment = BCPhotoUploadFragment()
    private val bcCongratulationFragment = BCCongratulationFragment()
    private val bcNewExperienceFragment = BCNewExperienceFragment()

    private lateinit var division: String
    private lateinit var district: String
    private lateinit var thana: String
    private lateinit var postOffice: String
    var address: String = ""
    var locationID: String = ""

    private var age = ""
    var birthDate: String? = ""
    private var subcategoriesID: String = ""
    private var experience: String = ""

    private var skilledBy : String =""
    private var ntvqfLevel : String =""
    private var clickPosition = -1


    private var eduDegree = ""
    private var eduLevel = ""
    private var passingYear = ""
    private var instName = ""
    private var educationType = ""
    private var hasEducation = ""

    private var categorySelectedPosition = -1
    //-------------api response value----------//

    private var isCVPostedRPS = ""
    private var nameRPS = ""
    private var emailRPS = ""
    private var userID = ""
    private var decodeId = ""
    private var userNameRPS = ""
    private var appsDate = ""
    private var ageRPS = ""
    private var experienseRPS = ""
    private var categoryIDRPS = ""
    private var genderRPS = ""
    private var resumeUpdateOn = ""
    private var isResumeUpdate = ""
    private var trainingId = ""
    private var userPicUrl = ""

    ///-----------socilaMedia---------/////////
    private var mGoogleSignInClient: GoogleApiClient? = null
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        stepProgressBar.visibility = View.GONE
        initializeGoogleRegistration()
        initializeFacebookRegistration()
        callbackManager = CallbackManager.Factory.create()
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
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    override fun checkInviteCodeEligibility() {
        val bdjobsUserSession = BdjobsUserSession(this@RegistrationBaseActivity)
        ApiServiceMyBdjobs.create().inviteCodeUserVerify(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                mobileNumber = bdjobsUserSession.userName,
                catId = getBlueCollarUserId().toString(),
                deviceID = getDeviceID()
        ).enqueue(object : Callback<InviteCodeUserVerifyModel> {
            override fun onFailure(call: Call<InviteCodeUserVerifyModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<InviteCodeUserVerifyModel>, response: Response<InviteCodeUserVerifyModel>) {
                try {
                    if (response.body()?.data?.get(0)?.eligible?.equalIgnoreCase("True")!!) {
                        val inviteCodeInfo = InviteCodeInfo(
                                userId = bdjobsUserSession.userId,
                                userType = "U",
                                pcOwnerID = "",
                                inviteCodeStatus = "0"
                        )
                        val bdjobsDB = BdjobsDB.getInstance(this@RegistrationBaseActivity)
                        Log.d("inviteCodeUserInfoReg", "userID = ${inviteCodeInfo.userId},\n" +
                                "userType = ${inviteCodeInfo.userType},\n" +
                                "inviteCodeStatus = ${inviteCodeInfo.inviteCodeStatus}")
                        doAsync {
                            bdjobsDB.inviteCodeUserInfoDao().insertInviteCodeUserInformation(inviteCodeInfo)
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }


    override fun goToHomePage() {

//        DatabaseUpdateJob.runJobImmediately()

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

        val intent = Intent(this@RegistrationBaseActivity, MainLandingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    //-----------------------  white Collar ------------------------//


    override fun gotToStepWhiteCollar() {

        categoryType = "0"
        transitFragment(wccategoryFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 17
    }

    override fun wcGoToStepSocialInfo() {

        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 34

    }

    override fun wcGoToStepName() {

        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 51

    }

    override fun wcGoToStepGender() {

        transitFragment(wcGenderFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 68

    }

    override fun wcGoToStepPhoneEmail() {

        transitFragment(wcPhoneEmailFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 85

    }

    override fun wcGoToStepPassword() {

        transitFragment(wcPasswordFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100

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

        categorySelectedPosition = position
        wccategoryFragment.wcGoToNextStep()
        categoryId = dataStorage.getCategoryIDByName(category)!!
        this.category = category
        Log.d("catagorySelected", "catagory $category")
        Log.d("catagorySelected", "categoryId $categoryId")
        Log.d("selectedPosition", " in activity $categorySelectedPosition")
        wccategoryFragment.getSelectedPosition(position)

    }

    override fun nameSelected(name: String) {
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

    override fun wcUserNameSelected(userName: String) {
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

        loadingProgressBar.visibility = View.VISIBLE
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
                        "sMid " + "" + socialMediaId + "\n"
        )

        ApiServiceMyBdjobs.create().createAccount(firstName, lastName, gender, wcEmail, userName, wcPassword, wcConfirmPass, mobileNumber, socialMediaId, isSMediaLogin, categoryType, userNameType, socialMediaType, categoryId, wcCountryCode, "", "").enqueue(object : Callback<CreateAccountModel> {
            override fun onFailure(call: Call<CreateAccountModel>, t: Throwable) {
                Log.d("ResponseTesrt", " onFailure ${t.message}")
                loadingProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<CreateAccountModel>, response: Response<CreateAccountModel>) {

                if (categoryType.equals("1", true)) {
                    Log.d("ResponseTesrt", " in blue collar condition ")
                    try {

                        Log.d("ResponseTesrt", " onResponse message ${response.body()!!.message}")
                        Log.d("ResponseTesrt", " onResponse statuscode ${response.body()!!.statuscode}")

                        if (response.isSuccessful) {


                            Log.d("ResponseTesrt", " 1 ")
                            if (response.body()!!.statuscode.equals("0", true)) {
                                Log.d("ResponseTesrt", " 2 ")
                                val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                                Log.d("ResponseTesrt", " isCvPosted ${isCvPosted}")
                                if (isCvPosted.equals("null", true)) {

                                    tempId = response.body()!!.data!!.get(0)!!.tmpId!!
                                    Log.d("ResponseTesrt", " tempId $tempId")
                                    Log.d("ResponseTesrt", " in first Condition")

                                    if (response.body()!!.statuscode.equals("2", true)) {

                                        toast(response.body()!!.message!!)
                                        Log.d("ResponseTesrt", " first condition")
                                    } else {
                                        bcGoToStepOtpCode()

                                        loadingProgressBar.visibility = View.GONE

                                        Log.d("ResponseTesrt", " second condition")
                                    }


                                }

                            } else if (response.body()!!.statuscode.equals("2", true)) {

                                loadingProgressBar.visibility = View.GONE
                                bcMobileNumberTIL?.showError(response.body()!!.message!!)


                            }


                        }
                    } catch (e: Exception) {
                        logException(e)
                    }


                } else if (categoryType.equals("0", true)) {

                    try {
                        if (response.isSuccessful) {
                            if (response.body()!!.statuscode.equals("0", true)) {

                                val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                                Log.d("ResponseTesrt", " isCvPosted ${isCvPosted}")
                                if (isCvPosted.equals("null", true)) {

                                    tempId = response.body()!!.data!!.get(0)!!.tmpId!!

                                    Log.d("ResponseTesrt", " tempId $tempId")
                                    Log.d("ResponseTesrt", " in first Condition")
                                    wcGoToStepMobileVerification()
                                    loadingProgressBar.visibility = View.GONE

                                } else {

                                    wcGoToStepCongratulation()
                                    loadingProgressBar.visibility = View.GONE
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



                                    bdjobsUserSession.createSession(isCVPostedRPS, nameRPS, emailRPS, userID, decodeId,
                                            userNameRPS, appsDate, ageRPS, experienseRPS, categoryIDRPS, genderRPS,
                                            resumeUpdateOn, isResumeUpdate, trainingId, userPicUrl)
                                }

                            } else if (response.body()!!.statuscode.equals("2", true)) {

                                loadingProgressBar.visibility = View.GONE
                                toast(response.body()!!.message!!)

                            }


                        }
                    } catch (e: Exception) {
                        logException(e)
                    }

                }

            }

        })


    }

    override fun showProgressBar() {

        loadingProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {

        loadingProgressBar.visibility = View.GONE
    }

    override fun wcOtpVerify() {

        loadingProgressBar.visibility = View.VISIBLE
        ApiServiceMyBdjobs.create().sendOtpToVerify(tempId, otpCode).enqueue(object : Callback<CreateAccountModel> {
            override fun onFailure(call: Call<CreateAccountModel>, t: Throwable) {
                Log.d("ResponseTesrt", " wcOtpVerify onFailure ${t.message}")
                loadingProgressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<CreateAccountModel>, response: Response<CreateAccountModel>) {

                try {
                    if (response.isSuccessful) {
                        Log.d("ResponseTesrt", " wcOtpVerify onResponse message ${response.body()!!.message}")
                        Log.d("ResponseTesrt", " wcOtpVerify onResponse statuscode ${response.body()!!.statuscode}")

                        /*    val username = response.body()!!.data!!.get(0)!!.userName.toString()

                            Log.d("ResponseTesrt", " wcOtpVerify name ${username}")*/
                        if (categoryType.equals("0", true)) {

                            try {
                                if (response.isSuccessful) {
                                    if (response.body()!!.statuscode.equals("0", true)) {

                                        val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                                        Log.d("ResponseTesrt", " wcOtpVerify isCvPosted ${isCvPosted}")
                                        if (!isCvPosted.equals("null", true)) {

                                            wcGoToStepCongratulation()
                                            loadingProgressBar.visibility = View.GONE
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



                                            bdjobsUserSession.createSession(isCVPostedRPS, nameRPS, emailRPS, userID, decodeId,
                                                    userNameRPS, appsDate, ageRPS, experienseRPS, categoryIDRPS, genderRPS,
                                                    resumeUpdateOn, isResumeUpdate, trainingId, userPicUrl)

                                            toast("Your account has been created successfully")

                                        }

                                    } else if (response.body()!!.statuscode.equals("3", true)) {

                                        loadingProgressBar.visibility = View.GONE
                                        wcOTPCodeTIL.showError(response.body()!!.message!!)
                                        toast(response.body()!!.message!!)
                                    } else if (response.body()!!.statuscode.equals("1", true)) {
                                        loadingProgressBar.visibility = View.GONE
                                        toast(response.body()!!.message!!)
                                    }


                                }
                            } catch (e: Exception) {
                                logException(e)
                            }

                        } else if (categoryType.equals("1", true)) {

                            try {
                                if (response.isSuccessful) {
                                    if (response.body()!!.statuscode.equals("0", true)) {

                                        val isCvPosted = response.body()!!.data!!.get(0)!!.isCvPosted.toString()

                                        Log.d("ResponseTesrt", " wcOtpVerify isCvPosted ${isCvPosted}")
                                        if (!isCvPosted.equals("null", true)) {


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


                                            Log.d("ResponseTesrt", "UserId $userID decodeid $decodeId")


                                            bdjobsUserSession.createSession(isCVPostedRPS, nameRPS, emailRPS, userID, decodeId,
                                                    userNameRPS, appsDate, ageRPS, experienseRPS, categoryIDRPS, genderRPS,
                                                    resumeUpdateOn, isResumeUpdate, trainingId, userPicUrl)

                                            bcGoToStepBirthDate()
                                            /*  toast("আপনার অ্যাকাউন্টটি তৈরি হয়েছে।")*/
                                            loadingProgressBar.visibility = View.GONE


                                        }

                                    } else if (response.body()!!.statuscode.equals("3", true)) {

                                        /* toast(response.body()!!.message!!)*/
                                        loadingProgressBar.visibility = View.GONE
                                        bcOTPCodeTIL.showError("সঠিক কোডটি টাইপ করুন")
                                    } else if (response.body()!!.statuscode.equals("1", true)) {
                                        loadingProgressBar.visibility = View.GONE
                                        toast(response.body()!!.message!!)
                                        bcOTPCodeTIL.showError(response.body()!!.message!!)
                                    }


                                }
                            } catch (e: Exception) {
                                logException(e)
                            }

                        }


                    }

                } catch (e: Exception) {

                    logException(e)

                }

            }


        })


    }

    override fun bcResendOtp() {
        loadingProgressBar.visibility = View.VISIBLE
        ApiServiceMyBdjobs.create().resendOtp(tempId, mobileNumber, "1").enqueue(object : Callback<ResendOtpModel> {
            override fun onFailure(call: Call<ResendOtpModel>, t: Throwable) {

                Log.d("resendOtp", " sjkafhsakfljh failuere")
            }

            override fun onResponse(call: Call<ResendOtpModel>, response: Response<ResendOtpModel>) {

                try {
                    Log.d("resendOtp", " sjkafhsakfljh ${response.message()}")
                    /*  toast(response.message())*/
                    loadingProgressBar.visibility = View.GONE

                } catch (e: Exception) {

                    logException(e)
                }


            }


        })

    }

    override fun getUserId(): String {
        return this.userID
    }

    override fun getDecodeId(): String {
        return this.decodeId
    }

    // -----------------------------  blue Collar start ------------------  //
    override fun goToStepBlueCollar() {

//        categoryType = "1"
//        transitFragment(bcCategoryFragment, R.id.registrationFragmentHolderFL, true)
//        stepProgressBar.visibility = View.VISIBLE
//        stepProgressBar.progress = 10

        bcGoToStepEducation()
    }

    override fun bcGoToStepName() {
        transitFragment(bcNameFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 20

    }

    override fun bcGoToStepGender() {
        transitFragment(bcGenderFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 30
        //bcGoToStepExperience()
    }

    override fun bcGoToStepMobileNumber() {

        transitFragment(bcMobileNumberFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 40
    }

    override fun bcGoToStepOtpCode() {

        transitFragment(bcOtpCodeFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 50
    }

    override fun bcGoToStepBirthDate() {

        transitFragment(bcBirthDateFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 60
    }

    override fun bcGoToStepAdress() {

        transitFragment(bcAdressFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 70
    }

    override fun bcGoToStepExperience() {

        transitFragment(bcNewExperienceFragment, R.id.registrationFragmentHolderFL, true)
        bcNewExperienceFragment.categoryInformation(category, categoryId)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 80
    }

    override fun bcGoToStepEducation() {

        transitFragment(bcEducationFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 90
    }

    override fun bcGoToStepPhotoUpload(hasEducation: String) {

        this.hasEducation = hasEducation

        loadingProgressBar.visibility = View.VISIBLE

        Log.d("catagorySelected", "hasEducation  ${this.hasEducation} ")
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100

        Log.d("ResponseTesrt",
                "userID: " + userID + "\n" +
                        "decodeId " + decodeId + "\n" +
                        "userName: " + userName + "\n" +
                        "address: " + address + "\n" +
                        "locationID: " + locationID + "\n" +
                        "birthDate: " + birthDate + "\n" +
                        "age " + age + "\n" +
                        "subcategoriesID: " + subcategoriesID + "\n" +
                        "experience: " + experience + "\n" +
                        "eduLevel " + eduLevel + "\n" +
                        "instName: " + instName + "\n" +
                        "educationType or inlineradio options " + educationType + "\n" +
                        "eduDegree " + eduDegree + "\n" +
                        "passingYear " + passingYear + "\n" +
                        "hasEducation " + hasEducation + "\n"

        )

        ApiServiceMyBdjobs.create().sendBlueCollarUserInfo(userID, decodeId, address, locationID, birthDate!!,
                experience, subcategoriesID, age, userName, eduLevel, instName,
                educationType, eduDegree, passingYear, hasEducation,skilledBy,ntvqfLevel,categoryId).enqueue(object : Callback<UpdateBlueCvModel> {
            override fun onFailure(call: Call<UpdateBlueCvModel>, t: Throwable) {
                Log.d("Ressdjg", " onFailure ${t.message}")
                loadingProgressBar.visibility = View.GONE
                toast("Server Error!! Please try again later")
            }

            override fun onResponse(call: Call<UpdateBlueCvModel>, response: Response<UpdateBlueCvModel>) {

                try {/*  toast("On response ")*/
                    Log.d("Ressdjg", " dkljgdslkjg ${response.body()!!.message}")
                    Log.d("Ressdjg", " dkljgdslkjg ${response.body().toString()}")

                    Log.d("Ressdjg", " dkljgdslkjg ${response.body()!!.statuscode}")
                    if (response.body()!!.statuscode.equals("0", true)) {

                        val bdjobsUserSession = BdjobsUserSession(this@RegistrationBaseActivity)
                        isCVPostedRPS = response.body()!!.data.get(0).isCvPosted
                        nameRPS = response.body()!!.data.get(0).name
                        emailRPS = response.body()!!.data.get(0).email
                        userID = response.body()!!.data.get(0).userId
                        decodeId = response.body()!!.data.get(0).decodId
                        userNameRPS = response.body()!!.data.get(0).userName
                        appsDate = response.body()!!.data.get(0).appsDate
                        ageRPS = response.body()!!.data.get(0).age
                        experienseRPS = response.body()!!.data.get(0).exp
                        categoryIDRPS = response.body()!!.data.get(0).catagoryId
                        genderRPS = response.body()!!.data.get(0).gender
                        resumeUpdateOn = response.body()!!.data.get(0).resumeUpdateON
                        isResumeUpdate = response.body()!!.data.get(0).isResumeUpdate
                        trainingId = response.body()!!.data.get(0).trainingId
                        userPicUrl = response.body()!!.data.get(0).userPicUrl


                        Log.d("ResponseTesrt", "UserId $userID decodeid $decodeId")


                        bdjobsUserSession.createSession(isCVPostedRPS, nameRPS, emailRPS, userID, decodeId,
                                userNameRPS, appsDate, ageRPS, experienseRPS, categoryIDRPS, genderRPS,
                                resumeUpdateOn, isResumeUpdate, trainingId, userPicUrl)



                        transitFragment(bcPhotoUploadFragment, R.id.registrationFragmentHolderFL, true)
                        loadingProgressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    logException(e)
                }


            }


        })

    }

    override fun bcGoToStepCongratulation() {

        transitFragment(bcCongratulationFragment, R.id.registrationFragmentHolderFL, true)
        stepProgressBar.visibility = View.GONE

    }

    override fun bcCategorySelected(category: String, position: Int) {

        categorySelectedPosition = position
        bcCategoryFragment.bcGoToNextStep()
        categoryId = dataStorage.getCategoryIDByBanglaName(category)!!
        this.category = category
        Log.d("catagorySelected", "catagory $category")
        Log.d("catagorySelected", "categoryId $categoryId")
        bcCategoryFragment.getSelectedPosition(position)


    }

    override fun getCategory(): String {

        return this.category

    }

    override fun bcGenderSelected(gender: String) {

        this.gender = gender
        Log.d("catagorySelected", "gender ${this.gender}")
        bcGenderFragment.goToNextStep()
    }

    override fun bcBirthDateAndAgeSelected(birthDate: String, age: String) {

        Log.d("catagorySelected", "birthDate nbmm $birthDate")

        if (TextUtils.isEmpty(birthDate)) {

            this.birthDate = ""

        } else {
            try {
                val initDate = SimpleDateFormat("dd/MM/yyyy").parse(birthDate)
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                val parsedDate = formatter.format(initDate)
                println(parsedDate)
                this.birthDate = parsedDate
                Log.d("catagorySelected", " birthDate ${this.birthDate}")


            } catch (e: RuntimeException) {

                Log.d("catagorySelected", " RuntimeException ${e.message}")
                e.printStackTrace()
            }

        }



        this.age = age

        Log.d("catagorySelected", "birthDate ll  ${this.birthDate} age ${this.age}")

    }

    override fun bcSelectedBlueCollarSubCategoriesIDandExperince(IDs: String, experience: String, skilledBy:String, ntvqfLevel:String, categoryID:String, category : String) {
        subcategoriesID = IDs
        this.experience = experience
        this.skilledBy = skilledBy
        this.ntvqfLevel = ntvqfLevel
        this.categoryId = categoryID
        this.category = category


        Log.d("catagorySelected", "subcategoriesID ${this.subcategoriesID} experience ${this.experience}")

    }

    override fun bcAddressSelected(district: String, thana: String, postOffice: String, address: String, locationID: String) {

        this.district = district
        this.thana = thana
        this.postOffice = postOffice
        this.address = address
        this.locationID = locationID


    }

    override fun bcEducationSelected(eduLevel: String, eduDegree: String, instName: String, passingYear: String, educationType: String) {

        this.eduLevel = eduLevel
        this.eduDegree = eduDegree
        this.instName = instName
        this.passingYear = passingYear
        this.educationType = educationType


        Log.d("catagorySelected", "eduLevel ${this.eduLevel}" +
                "  eduDegree ${this.eduDegree} instName ${this.instName}" +
                " passingYear ${this.passingYear} educationType ${this.educationType}")


    }

    override fun bcGetAge(): String {
        return age
    }

    //-------------blue Collar End-----------------//

    override fun onBackPressed() {

        setProgreesBar()

        val curretFragment = fragmentManager.findFragmentById(R.id.registrationFragmentHolderFL)

        Log.d("curretFragment", "curretFragment $curretFragment")

        when (curretFragment) {
            bcPhotoUploadFragment -> Toast.makeText(this, "আগের পেজে যেতে পারবেন না ", Toast.LENGTH_SHORT).show()
            bcBirthDateFragment -> Toast.makeText(this, "আগের পেজে যেতে পারবেন না", Toast.LENGTH_SHORT).show()
            bcCongratulationFragment -> goToHomePage()
            wcCongratulationFragment -> goToHomePage()
            else -> super.onBackPressed()
        }


    }

    private fun setProgreesBar() {
        val currentFragment = fragmentManager.findFragmentById(R.id.registrationFragmentHolderFL)
        Log.d("stepChange", "FragmentLive: $currentFragment")

        when (currentFragment) {

            registrationLandingFragment -> {
                stepProgressBar.visibility = View.GONE

            }
            wccategoryFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 17

            }

            wcSocialInfoFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 34

            }
            wcNameFragment -> {

                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 51

            }
            wcGenderFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 68

            }
            wcPhoneEmailFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 85

            }
            wcPasswordFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 100

            }

            // blue collar

            bcNameFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 20

            }
            bcGenderFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 30
                Log.d("stepChange", " in wccategoryFragment")
            }
            bcMobileNumberFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 40

            }
            bcOtpCodeFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 50

            }
            bcBirthDateFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 60

            }
            bcAdressFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 70

            }
            bcNewExperienceFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 80

            }
            bcEducationFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 90

            }
            bcPhotoUploadFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 100

            }

            bcCategoryFragment -> {
                stepProgressBar.visibility = View.VISIBLE
                stepProgressBar.progress = 10

            }


        }

    }

    override fun clearData() {


    }

    override fun getINLROData(): String {
        return educationType
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d("onActivityResultPhoto", "requestCode: $requestCode, resultCode:$resultCode, data:$data")
        try {
            if (resultCode != RESULT_CANCELED && requestCode != null && data != null) {


                bcPhotoUploadFragment.onActivityResult(requestCode, resultCode, data)
                callbackManager?.onActivityResult(requestCode, resultCode, data)

                // LISessionManager.getInstance(this@RegistrationBaseActivity).onActivityResult(this@RegistrationBaseActivity, requestCode, resultCode, data)
                if (requestCode == Constants.RC_SIGN_IN) {

                    val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                    Log.d("GoffleSignIn", " isSuccess ${result.isSuccess}")
                    Log.d("GoffleSignIn", " signInAccount ${result.signInAccount}")
                    if (result.isSuccess) {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = result.signInAccount
                        val sid = account?.id
                        val semial = account?.email
                        var name = account?.displayName


                        Log.d("GoogleSignIn", "sid:$sid \n semial:$semial  \n sname: $name")
                        signOutFromGoogle()
                        /* socialMediaMapping(sid, semial, Constants.SOCIAL_MEDIA_GOOGLE)*/


                        // Google Sign In was successful, authenticate with Firebase

                        //Toast.makeText(RegistrationFina.this, "Success", Toast.LENGTH_SHORT).show();
                        val fname = account!!.givenName
                        val lname = account.familyName
                        socialMediaId = account.id!!
                        isSMediaLogin = "True"
                        socialMediaType = "G"
                        name = fname + " " + lname

                        this.name = name

                        Log.d("Gmail", "${this.name}  ${this.wcEmail} ")

                        wcEmail = account.email!!
                        userNameType = ""
                        gender = ""

                        if (categoryType.equals("0", ignoreCase = true)) {


                            isSMediaLogin = "True"
                            socialMediaType = "G"
                            /* whiteCollarSocialLoginFragment.stopProgressDialog()*/

                            wcGoToStepName()

                        } else {
                            bcNameFragment.setName()


                        }
                        /* firebaseAuthWithGoogle(account)*/


                    } else {
                        // Google Sign In failed, update UI appropriately
                        toast("Please sign in to google first to complete your sign in by goole")
                    }
                }


            }

        } catch (e: Exception) {
            logException(e)
        }


    }

    private fun signOutFromGoogle() {
        if (mGoogleSignInClient?.isConnected!!) {
            Auth.GoogleSignInApi.signOut(mGoogleSignInClient)
            mGoogleSignInClient?.disconnect()
            mGoogleSignInClient?.connect()
        }
    }

    override fun regWithLinkedIn() {

        /*LISessionManager.getInstance(this@RegistrationBaseActivity).init(this@RegistrationBaseActivity, buildScope(), object : AuthListener {

            override fun onAuthSuccess() {

                val apiHelper = APIHelper.getInstance(this@RegistrationBaseActivity)

                apiHelper.getRequest(this@RegistrationBaseActivity, Constants.LINKEDIN_REQUEST_URL, object : ApiListener {

                    override fun onApiSuccess(result: ApiResponse) {
                        val response = result.responseDataAsJson
                        try {
                            var lemail = ""
                            var sMid = ""
                            if (response.has("emailAddress")) {
                                lemail = response.getString("emailAddress")
                            }


                            if (response.has("id")) {
                                sMid = response.getString("id")
                            }

                            *//* if (response.has("id")) {
                                 sMid = response.getString("id")
                             }*//*

                            *//* socialMediaMapping(sMid, lemail, Constants.SOCIAL_MEDIA_LINKEDIN)*//*
                            Log.d("signInWithLinkedIn", "sMid:$sMid \n lemail: $lemail")


                            *//* val  fname = account!!.givenName
                             val lname = account.familyName*//*
                            socialMediaId = sMid
                            isSMediaLogin = "True"
                            socialMediaType = "L"
                            *//*  name = fname + " " + lname*//*

                            *//*  this.name = name*//*

                            wcEmail = lemail
                            userNameType = ""
                            gender = ""

                            if (categoryType.equals("0", ignoreCase = true)) {


                                isSMediaLogin = "True"
                                socialMediaType = "L"
                                *//* whiteCollarSocialLoginFragment.stopProgressDialog()*//*
                                wcGoToStepName()

                            } else {
                                bcNameFragment.setName()


                            }
                            *//* firebaseAuthWithGoogle(account)*//*


                        } catch (e: Exception) {
                            logException(e)

                            e.printStackTrace()
                            toast(e.toString())
                            toast("Some internal errors have been occurred,please try again later!")
                        }


                    }

                    override fun onApiError(error: LIApiError) {
                        logException(error)
                        toast(error.toString())

                    }
                })


            }

            override fun onAuthError(error: LIAuthError) {
                error(error)
                toast(error.toString())

            }
        }, true)

*/
    }

    override fun regWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this@RegistrationBaseActivity, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {

                        val request = GraphRequest.newMeRequest(
                                loginResult.accessToken
                        ) { `object`, response ->
                            Log.d("FBLoginActivity", response.toString())
                            println("LoginActivity " + response.toString())

                            // Application code
                            try {
                                var semail = ""
                                var birthday = ""
                                var sgender = ""
                                var sfirstname = ""
                                var slastname = ""
                                var sMid = ""
                                var fbPicUrl = ""

                                /*  var semail: String? = null
                                  var sMid: String? = null

                                  try {
                                      if (profileData.has(Constants.FB_KEY_EMAIL)) {
                                          semail = profileData.getString(Constants.FB_KEY_EMAIL)
                                      }
                                      if (profileData.has(Constants.FB_KEY_ID)) {
                                          sMid = profileData.getString(Constants.FB_KEY_ID)
                                      }
                                  } catch (e: Exception) {
                                      e.printStackTrace()
                                  }*/


                                signOutFromFacebook()

                                if (`object`.has("email")) {
                                    semail = `object`.getString("email")
                                    wcEmail = semail
                                }

                                if (`object`.has("birthday")) {
                                    birthday = `object`.getString("birthday")
                                }

                                if (`object`.has("gender")) {
                                    sgender = `object`.getString("gender")
                                    Log.d("Gender", " $sgender")
                                }

                                if (`object`.has("first_name")) {
                                    sfirstname = `object`.getString("first_name")
                                }
                                if (`object`.has("last_name")) {
                                    slastname = `object`.getString("last_name")
                                }
                                if (`object`.has("id")) {
                                    sMid = `object`.getString("id")
                                }
                                //   sname  = object.getString("name");

                                if (sgender == "female") {
                                    gender = "F"
                                } else if (sgender == "male") {
                                    gender = "M"
                                }

                                Log.d("Gender", "$sgender")

                                name = "$sfirstname $slastname"
                                wcEmail = semail

                                isSMediaLogin = "True"
                                socialMediaType = "F"
                                userNameType = ""
                                socialMediaId = sMid

                                fbPicUrl = "https://graph.facebook.com/$sMid/picture?type=large"
                                Log.d("MobileNumberVer2",
                                        "fbPicUrl: $fbPicUrl")

                                if (categoryType.equals("0", ignoreCase = true)) {

                                    wcGoToStepName()
                                } else {


                                    bcNameFragment.setName()

                                }
                                // info.setText(email+" "+birthday+" "+gender+" "+name+" "+fbid);

                                // info.setText(email+" "+birthday+" "+gender+" "+name);

                                println("having value $semail $birthday $gender $name $sMid")


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            // 01/31/1980 format
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender,birthday,cover,picture.type(large),first_name,last_name")
                        request.parameters = parameters
                        request.executeAsync()
                        //app code
                    }

                    override fun onCancel() {

                        /* whiteCollarSocialLoginFragment.stopProgressDialog()*/
                        Toast.makeText(this@RegistrationBaseActivity, "Please sign in to facebook first to complete your sign in by facebook", Toast.LENGTH_LONG).show()

                    }

                    override fun onError(exception: FacebookException) {

                        /* whiteCollarSocialLoginFragment.stopProgressDialog()*/
                        Toast.makeText(this@RegistrationBaseActivity, exception.toString(), Toast.LENGTH_LONG).show()
                        println("gfkhghgj " + exception.toString())

                        //app code
                    }
                })
    }

    override fun regWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient)
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    private fun initializeGoogleRegistration() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.GOOGLE_SIGN_IN_CLIENT_ID)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleApiClient.Builder(this@RegistrationBaseActivity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private fun initializeFacebookRegistration() {
        callbackManager = CallbackManager.Factory.create()
    }

    override fun getName(): String {
        return this.name
    }

    override fun getEmail(): String {
        return wcEmail
    }

    override fun getGender(): String {
        return this.gender
    }

    private fun signOutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return // already logged out
        }
        val request = GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        bcPhotoUploadFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun setItemClick(position: Int) {
        clickPosition = position
    }

    override fun getItemClick(): Int {
        return clickPosition
    }

    override fun showEditDialog(item: AddExpModel) {
        bcNewExperienceFragment.showEditDialog(item)
    }

    override fun getCategoryId(): String {

        return categoryId
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(registrationBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))

            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }
}
