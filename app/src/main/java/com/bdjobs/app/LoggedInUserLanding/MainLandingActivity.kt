package com.bdjobs.app.LoggedInUserLanding

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FileInfo
import com.bdjobs.app.API.ModelClasses.InviteCodeHomeModel
import com.bdjobs.app.API.ModelClasses.InviteCodeUserStatusModel
import com.bdjobs.app.API.ModelClasses.StatsModelClass
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.AppliedJobs.AppliedJobsActivity
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.InviteCodeInfo
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.bdjobs.app.editResume.PhotoUploadActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_landing.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainLandingActivity : Activity(), HomeCommunicator {
    override fun setShortListFilter(filter: String) {
        this.shortListFilter = filter
    }

    override fun getShortListFilter(): String {
        return shortListFilter
    }

    private var shortListFilter: String = ""


    override fun goToShortListedFragment(deadline: Int) {
        bottom_navigation?.selectedItemId = R.id.navigation_shortlisted_jobs
    }

    override fun getInviteCodepcOwnerID(): String? {
        return pcOwnerID
    }

    override fun getInviteCodeStatus(): String? {
        return inviteCodeStatus
    }

/*    override fun goToEmployerViewedMyResume(from: String) {
        startActivity<EmployersBaseActivity>("from" to from)
    }*/

    override fun getInviteCodeUserType(): String? {
        return inviteCodeuserType
    }

    private lateinit var bdjobsDB: BdjobsDB

    private val homeFragment = HomeFragment()
    private val hotJobsFragment = HotJobsFragment()
    private val hotJobsFragmentnew = HotJobsFragmentNew()
    private val moreFragment = MoreFragment()
    private val shortListedJobFragment = ShortListedJobFragment()
    private val mybdjobsFragment = MyBdjobsFragment()
    private lateinit var session: BdjobsUserSession
    private var lastMonthStats: List<StatsModelClassData?>? = null
    private var allTimeStats: List<StatsModelClassData?>? = null
    private var inviteCodeuserType: String? = null
    private var pcOwnerID: String? = null
    private var inviteCodeStatus: String? = null
    var cvUpload: String = "" // if this value = 0 or 4 then cv file is uploaded else not uploaded


    override fun isGetCvUploaded(): String {
        return cvUpload
    }

    override fun decrementCounter() {
        shortListedJobFragment.decrementCounter()
    }

    override fun scrollToUndoPosition(position: Int) {
        shortListedJobFragment.scrollToUndoPosition(position)
    }

    private var time: String = ""

    override fun goToEmployerViewedMyResume(from: String) {
        startActivity<EmployersBaseActivity>(
                "from" to from,
                "time" to time
        )
    }

    override fun setTime(time: String) {
        this.time = time
    }

    override fun goToAppliedJobs() {
        startActivity<AppliedJobsActivity>("time" to time)
    }

    override fun getLastStatsData(): List<StatsModelClassData?>? {
        return lastMonthStats
    }

    override fun getAllStatsData(): List<StatsModelClassData?>? {
        return allTimeStats
    }


    override fun goToInterviewInvitation(from: String) {
        startActivity<InterviewInvitationBaseActivity>("from" to from)
    }


    override fun backButtonClicked() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (hotJobsFragment.getWebviewBacKStack()) {
            super.onBackPressed()
        }
    }


    override fun goToFollowedEmployerList(from: String) {
        startActivity<EmployersBaseActivity>(
                "from" to from,
                "time" to time
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_landing)
        bdjobsDB = BdjobsDB.getInstance(this@MainLandingActivity)
        session = BdjobsUserSession(applicationContext)
        Crashlytics.setUserIdentifier(session.userId)
        bottom_navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation?.selectedItemId = R.id.navigation_home


        if (isBlueCollarUser()) {
            getInviteCodeInformation()
            getUserStatus(userId = session.userId!!, decodeId = session.decodId!!, invitedUserId = session.userId!!)
        }

        getStatsData("0")
        getStatsData("1")
        getIsCvUploaded()

        tetsLog()
    }

    private fun getInviteCodeInformation() {
        doAsync {
            val inviteCodeUserInfo = bdjobsDB.inviteCodeUserInfoDao().getInviteCodeInformation(session.userId!!)
            uiThread {

                if (inviteCodeUserInfo.isNullOrEmpty()) {

                    Log.d("inviteCodeUserInfo", "userID = ${session.userId},\n" +
                            "decodeID = ${session.decodId},\n" +
                            "mobileNumber = ${session.userName},\n" +
                            "catId = ${getBlueCollarUserId()},\n" +
                            "deviceID = ${getDeviceID()}")
                    updateInviteCodeOwnerInformation()

                } else {
                    inviteCodeuserType = inviteCodeUserInfo[0].userType
                    pcOwnerID = inviteCodeUserInfo[0].pcOwnerID
                    inviteCodeStatus = inviteCodeUserInfo[0].inviteCodeStatus


                    if (!inviteCodeuserType?.equalIgnoreCase("u")!!) {
                        updateInviteCodeOwnerInformation()
                    }

                    Log.d("inviteCodeUserInfo", "pcOwnerID = $pcOwnerID")
                }
            }
        }
    }

    private fun updateInviteCodeOwnerInformation() {
        ApiServiceMyBdjobs.create().getInviteCodeUserOwnerInfo(
                userID = session.userId,
                decodeID = session.decodId,
                mobileNumber = session.userName,
                catId = getBlueCollarUserId().toString(),
                deviceID = getDeviceID()
        ).enqueue(object : Callback<InviteCodeHomeModel> {
            override fun onFailure(call: Call<InviteCodeHomeModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<InviteCodeHomeModel>, response: Response<InviteCodeHomeModel>) {

                if (response.body()?.statuscode == Constants.api_request_result_code_ok) {

                    val inviteCodeInfo = InviteCodeInfo(
                            userId = session.userId,
                            userType = response.body()?.data?.get(0)?.userType,
                            pcOwnerID = response.body()?.data?.get(0)?.pcOwnerID,
                            inviteCodeStatus = response.body()?.data?.get(0)?.inviteCodeStatus
                    )
                    Log.d("inviteCodeUserInfo", "userID = ${session.userId},\n" +
                            "userType = ${response.body()?.data?.get(0)?.userType},\n" +
                            "pcOwnerID = ${response.body()?.data?.get(0)?.pcOwnerID},\n" +
                            "inviteCodeStatus = ${response.body()?.data?.get(0)?.inviteCodeStatus}")

                    doAsync {
                        bdjobsDB.inviteCodeUserInfoDao().insertInviteCodeUserInformation(inviteCodeInfo)
                    }
                    inviteCodeuserType = inviteCodeInfo.userType
                    pcOwnerID = inviteCodeInfo.pcOwnerID
                    inviteCodeStatus = inviteCodeInfo.inviteCodeStatus
                }
            }
        })
    }

    override fun goToKeywordSuggestion() {
        val intent = Intent(this@MainLandingActivity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, Constants.key_jobtitleET)
        intent.putExtra(key_typedData, "")
        window.exitTransition = null
        startActivityForResult(intent, BdjobsUserRequestCode)
    }

    override fun goToFavSearchFilters() {
        startActivity<FavouriteSearchBaseActivity>()
    }

    override fun goToJoblistFromLastSearch() {
        startActivity<JobBaseActivity>("from" to "lastsearch")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                val from = data?.getStringExtra(key_from)
                startActivity<JobBaseActivity>(
                        Constants.key_jobtitleET to typedData)
            }
        }
    }

    override fun goToJobSearch(favID: String) {
        startActivity<JobBaseActivity>("from" to "favsearch", "filterid" to favID)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                transitFragment(homeFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shortlisted_jobs -> {
                transitFragment(shortListedJobFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hotjobs -> {
                transitFragment(hotJobsFragmentnew, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_mybdjobs -> {
                transitFragment(mybdjobsFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_more -> {
                //  transitFragment(appliedJobsFragment, R.id.landingPageFragmentHolderFL)
                transitFragment(moreFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    fun tetsLog() {

        Log.d("XZXfg", "\nisCvPosted = ${session.isCvPosted}\n" +
                "userPicUrl = ${session.userPicUrl}\n" +
                "name = ${session.fullName}\n" +
                "email = ${session.email}\n" +
                "userId = ${session.userId}\n" +
                "decodId = ${session.decodId}\n" +
                "userName = ${session.userName}\n" +
                "AppsDate = ${session.AppsDate}\n" +
                "age = ${session.age}\n" +
                "exp = ${session.exp}\n" +
                "catagoryId = ${session.catagoryId}\n" +
                "gender = ${session.gender}\n" +
                "resumeUpdateON = ${session.resumeUpdateON}\n" +
                "IsResumeUpdate = ${session.IsResumeUpdate}\n" +
                "trainingId = ${session.trainingId}\n")
    }

    override fun shortListedClicked(Position: Int) {
        startActivity<JobBaseActivity>("from" to "shortListedJob", "position" to Position, "shortListFilter" to shortListFilter)

    }

    private fun getStatsData(activityDate: String) {
        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = session.userId,
                decodeId = session.decodId,
                isActivityDate = activityDate,
                trainingId = session.trainingId,
                isResumeUpdate = session.IsResumeUpdate

        ).enqueue(object : Callback<StatsModelClass> {
            override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {

                try {
                    if (activityDate == "0") {
                        allTimeStats = response.body()?.data
                    } else if (activityDate == "1") {
                        lastMonthStats = response.body()?.data
                    }

                    Log.d("respp", "$allTimeStats /n $lastMonthStats")
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }


    private fun getUserStatus(userId: String, decodeId: String, invitedUserId: String) {

        ApiServiceMyBdjobs.create().getInviteCodeUserStatus(
                userID = userId,
                decodeID = decodeId,
                invited_user_id = invitedUserId
        ).enqueue(
                object : Callback<InviteCodeUserStatusModel> {
                    override fun onFailure(call: Call<InviteCodeUserStatusModel>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InviteCodeUserStatusModel>, response: Response<InviteCodeUserStatusModel>) {
                        try {
                            if (response.isSuccessful) {
                                val photoInfo = response.body()!!.data[0].photoInfo
                                val educationInfo = response.body()!!.data[0].educationInfo
                                val personalInfo = response.body()!!.data[0].personalInfo
                                val skills = response.body()!!.data[0].skills

                                if (photoInfo.equalIgnoreCase("True") &&
                                        educationInfo.equalIgnoreCase("True") &&
                                        personalInfo.equalIgnoreCase("True") &&
                                        skills.equalIgnoreCase("True")
                                ) {
                                    Log.d("getUserStatus","everything is filled up")
                                } else {
                                    showCategoryDialog(
                                            response.body()!!.data[0].name,
                                            response.body()!!.data[0].category,
                                            response.body()!!.data[0].photoUrl,
                                            personalInfo,
                                            educationInfo,
                                            photoInfo,
                                            response.body()!!.data[0].createdDate,
                                            skills
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
        )

    }

    private fun showCategoryDialog(name: String, category: String, photoUrl: String, personalInfo: String, educationInfo: String, photoInfo: String, createdDate: String, skillInfo: String) {

        Log.d("showCategoryDialog", "" +
                "educationInfo: $educationInfo" +
                "personalInfo: $personalInfo" +
                "photoInfo:$photoInfo" +
                "name: $name")

        val dialog = Dialog(this@MainLandingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.invite_user_details_layout)


        val nameTV = dialog.findViewById(R.id.nameTV) as TextView
        val categoryTv = dialog.findViewById(R.id.categoryTv) as TextView
        val categoryDateTv = dialog.findViewById(R.id.categoryDateTv) as TextView
        val profilePicIV = dialog.findViewById(R.id.profilePicIMGV) as ImageView
        val personalImageView = dialog.findViewById(R.id.personalIMGV) as ImageView
        val educationaImageView = dialog.findViewById(R.id.educationIMGV) as ImageView
        val skillIMGV = dialog.findViewById(R.id.skillIMGV) as ImageView
        val photInfoImageView = dialog.findViewById(R.id.pictureIMGV) as ImageView
        val cancelIconImgv = dialog.findViewById(R.id.cancelIconImgv) as ImageView


        nameTV.text = name
        categoryTv.text = category
        categoryDateTv.text = createdDate.toBanglaDigit()

        profilePicIV.loadCircularImageFromUrl(photoUrl)

        if (personalInfo.equals("True", ignoreCase = true)) {
            personalImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            personalImageView.setBackgroundResource(R.drawable.resume_add_icon)
            personalImageView.setOnClickListener {
                setClickListener("personal")
                cancelIconImgv.performClick()
            }

        }

        if (educationInfo.equals("True", ignoreCase = true)) {
            educationaImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            educationaImageView.setBackgroundResource(R.drawable.resume_add_icon)
            educationaImageView.setOnClickListener {
                setClickListener("education")
                cancelIconImgv.performClick()
            }
        }

        if (photoInfo.equals("True", ignoreCase = true)) {
            photInfoImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            photInfoImageView.setBackgroundResource(R.drawable.resume_add_icon)
            photInfoImageView.setOnClickListener {
                setClickListener("photo")
                cancelIconImgv.performClick()
            }
        }

        if (skillInfo.equals("True", ignoreCase = true)) {
            skillIMGV.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            skillIMGV.setBackgroundResource(R.drawable.resume_add_icon)
            skillIMGV.setOnClickListener {
                setClickListener("experience")
                cancelIconImgv.performClick()
            }
        }



        dialog.setCancelable(true)
        dialog.show()

        cancelIconImgv.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setClickListener(clickItem: String) {

        when (clickItem) {
            "personal" -> {
                startActivity<PersonalInfoActivity>("name" to "null", "personal_info_edit" to "addDirect")
            }
            "experience" -> {
                startActivity<EmploymentHistoryActivity>("name" to "null", "emp_his_add" to "addDirect")
            }
            "education" -> {
                startActivity<AcademicBaseActivity>("name" to "null", "education_info_add" to "addDirect")
            }
            "photo" -> {
                startActivity<PhotoUploadActivity>()
            }
        }
    }
    private fun getIsCvUploaded() {
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = session.userId,
                decodeID = session.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)
                toast("${t.toString()}")
            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                //toast("${response.body()?.statuscode}")
                if (response.isSuccessful){
                    cvUpload = response.body()?.statuscode!!
                    Log.d("value", "val " + cvUpload)

                }
            }

        })

    }

}
