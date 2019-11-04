package com.bdjobs.app.Login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ProgressBar
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LastUpdateModel
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListData
import com.bdjobs.app.API.ModelClasses.StatsModelClass
import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Jobs.JobDetailsFragment
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationBaseActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.key_go_to_home
import com.bdjobs.app.Web.WebActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_base.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginBaseActivity : Activity(), LoginCommunicator, ConnectivityReceiver.ConnectivityReceiverListener {

    private val jobDetailsFragment = JobDetailsFragment()
    private val loginUserNameFragment = LoginUserNameFragment()
    private val loginPasswordFragment = LoginPasswordFragment()
    private val loginOTPFragment = LoginOTPFragment()
    private val socialAccountListFragment = SocialAccountListFragment()
    private var mSnackBar: Snackbar? = null
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var userId: String? = null
    private var fullName: String? = null
    private var imageUrl: String? = null
    private var userName: String? = null
    private var socialLoginAccountDataList: List<SocialLoginAccountListData?>? = null
    private var goToHome = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)
        transitFragment(loginUserNameFragment, R.id.loginFragmentHolderFL)
        intent?.extras?.getBoolean(key_go_to_home)?.let { goHome ->
            goToHome = goHome
            Log.d("goToHome", "goToHome: ${goToHome}")
        }
    }

    override fun backButtonClicked() {
        onBackPressed()
    }

    override fun goToWebActivity(url: String, from: String) {
        startActivity<WebActivity>("url" to url, "from" to from)
    }


    override fun goToPasswordFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        debug("goToPasswordFragment, userName:$userName \n fullName:$fullName \n userId:$userId \n imageUrl:$imageUrl \n")
        transitFragment(loginPasswordFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToHomePage() {
        DatabaseUpdateJob.runJobImmediately()
        if (goToHome) {
            val intent = Intent(this@LoginBaseActivity, MainLandingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } else {
            getLastUpdateFromServer(this@LoginBaseActivity)
            if (!Constants.isDeviceInfromationSent) {
                Log.d("splashActivity", "token aaa")
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
                    val token = instanceIdResult.token
                    Constants.sendDeviceInformation(token, this@LoginBaseActivity)
                }
            }
        }
    }

    override fun getSocialLoginAccountDataList(): List<SocialLoginAccountListData?>? {
        return socialLoginAccountDataList
    }

    override fun getGoToHome(): Boolean? {
        return goToHome
    }

    override fun goToSocialAccountListFragment(socialLoginAccountDataList: List<SocialLoginAccountListData?>?) {
        this.socialLoginAccountDataList = socialLoginAccountDataList
        transitFragment(socialAccountListFragment, R.id.loginFragmentHolderFL, true)
    }


    override fun goToOtpFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        debug("goToOtpFragment, userName:$userName \n fullName:$fullName \n userId:$userId \n imageUrl:$imageUrl \n")
        transitFragment(loginOTPFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToRegistrationActivity() {
        startActivity<RegistrationBaseActivity>()
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    override fun getFullName(): String? {
        return fullName
    }

    override fun getUserId(): String? {
        return userId
    }

    override fun getImageUrl(): String? {
        return imageUrl
    }

    override fun getUserName(): String? {
        return userName
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(loginBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
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

    private fun getLastUpdateFromServer(context: Context) {
        val bdjobsUserSession = BdjobsUserSession(context)
        ApiServiceMyBdjobs.create().getLastUpdate(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId
        ).enqueue(object : Callback<LastUpdateModel> {
            override fun onFailure(call: Call<LastUpdateModel>, t: Throwable) {
                error("onFailure", t)
                getMybdjobsCountData("1",this@LoginBaseActivity)
            }

            override fun onResponse(call: Call<LastUpdateModel>, response: Response<LastUpdateModel>) {
                try {
                    Constants.changePassword_Eligibility = response.body()?.data?.get(0)?.changePassword_Eligibility!!
                    bdjobsUserSession.updateIsResumeUpdate(response.body()?.data?.get(0)?.isResumeUpdate!!)
                    bdjobsUserSession.updateIsCvPosted(response.body()?.data?.get(0)?.isCVPosted!!)
                    bdjobsUserSession.updateTrainingId(response.body()?.data?.get(0)?.trainingId!!)
                    bdjobsUserSession.updateEmail(response.body()?.data?.get(0)?.email!!)
                    bdjobsUserSession.updateFullName(response.body()?.data?.get(0)?.name!!)
                    bdjobsUserSession.updateUserName(response.body()?.data?.get(0)?.userName!!)
                    bdjobsUserSession.updateCatagoryId(response.body()?.data?.get(0)?.catId!!)
                    bdjobsUserSession.updateUserPicUrl(response.body()?.data?.get(0)?.userPicUrl?.trim()!!)
                    bdjobsUserSession.updateJobApplyLimit(response.body()?.data?.get(0)?.jobApplyLimit)
                    bdjobsUserSession.updateJobApplyThreshold(response.body()?.data?.get(0)?.appliedJobsThreshold)
//                    try {
//                        Constants.appliedJobLimit = response.body()?.data?.get(0)?.jobApplyLimit!!.toInt()
//                    } catch (e: Exception) {
//                    }
//                    Constants.applyRestrictionStatus = response.body()?.data?.get(0)?.applyRestrictionStatus!!
//                    Constants.appliedJobsThreshold = response.body()?.data?.get(0)?.appliedJobsThreshold!!.toInt()
                    getMybdjobsCountData("1",this@LoginBaseActivity)
                    //Log.d("changePassword", "changePassword_Eligibility = ${response.body()?.data?.get(0)?.changePassword_Eligibility!!}")
                } catch (e: Exception) {
                    logException(e)
                    getMybdjobsCountData("1",this@LoginBaseActivity)
                }
            }

        })
    }

    private fun getMybdjobsCountData(activityDate: String,context: Context) {
        val bdjobsUserSession = BdjobsUserSession(context)
        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                isActivityDate = activityDate,
                trainingId = bdjobsUserSession.trainingId,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate
        ).enqueue(object : Callback<StatsModelClass> {
            override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {
finish()
            }

            override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {

                try {
                    var jobsApplied:String?=""
                    var emailResume:String?=""
                    var viewdResume:String?=""
                    var followedEmployers:String?=""
                    var interviewInvitation:String?=""
                    var employerMessage:String?=""

                    response?.body()?.data?.forEach {itt ->
                        when(itt?.title){
                            Constants.session_key_mybdjobscount_jobs_applied->{jobsApplied = itt?.count}
                            Constants.session_key_mybdjobscount_times_emailed_resume->{emailResume = itt?.count}
                            Constants.session_key_mybdjobscount_employers_viwed_resume->{viewdResume = itt?.count}
                            Constants.session_key_mybdjobscount_employers_followed->{followedEmployers = itt?.count}
                            Constants.session_key_mybdjobscount_interview_invitation->{interviewInvitation = itt?.count}
                            Constants.session_key_mybdjobscount_message_by_employers->{employerMessage = itt?.count}
                        }

                    }

                    if (activityDate == "0") {
                        //alltime
                        bdjobsUserSession.insertMybdjobsAlltimeCountData(
                                jobsApplied =jobsApplied,
                                emailResume = emailResume,
                                employerViewdResume = viewdResume,
                                followedEmployers = followedEmployers,
                                interviewInvitation = interviewInvitation,
                                messageByEmployers = employerMessage
                        )
                    } else if (activityDate == "1") {
                        //last_moth
                        try {
                            Constants.appliedJobsCount = jobsApplied!!.toInt()
                        } catch (e: Exception) {
                        }
                        bdjobsUserSession.insertMybdjobsLastMonthCountData(
                                jobsApplied =jobsApplied,
                                emailResume = emailResume,
                                employerViewdResume = viewdResume,
                                followedEmployers = followedEmployers,
                                interviewInvitation = interviewInvitation,
                                messageByEmployers = employerMessage
                        )
                    }
                    finish()
                } catch (e: Exception) {
                    logException(e)
                    finish()
                }
            }

        })
    }



}
