package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.Databases.Internal.*
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.loadFirstTime
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.my_assessment_filter_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_interview_invitation_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*

class HomeFragment : Fragment(), BackgroundJobBroadcastReceiver.BackgroundJobListener {


    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
    private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        nameTV.text = bdjobsUserSession.fullName
        emailTV.text = bdjobsUserSession.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)

        if (!loadFirstTime) {
            showData()
        }
    }

    private fun showData() {
        Log.i("DatabaseUpdateJob", "Home Fragment Start: ${Calendar.getInstance().time}")
        showFavouriteSearchFilters()
        showJobInvitation()
        showCertificationInfo()
        showFollowedEmployers()
    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(backgroundJobBroadcastReceiver, intentFilter)
        BackgroundJobBroadcastReceiver.backgroundJobListener = this
    }

    override fun onPause() {
        super.onPause()
        activity.unregisterReceiver(backgroundJobBroadcastReceiver)
    }

    override fun jobInvitationSyncComplete() {
        Log.d("broadCastCheck", "jobInvitationSyncComplete")
        showJobInvitation()
    }

    override fun certificationSyncComplete() {
        Log.d("broadCastCheck", "certificationSyncComplete")
        showCertificationInfo()
    }

    override fun followedEmployerSyncComplete() {
        Log.d("broadCastCheck", "followedEmployerSyncComplete")
        showFollowedEmployers()
    }

    override fun favSearchFilterSyncComplete() {
        Log.d("broadCastCheck", "favSearchFilterSyncComplete")
        showFavouriteSearchFilters()
    }

    private fun showFollowedEmployers() {
        doAsync {
            val followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {


                followedEmployerView.hide()
                if (followedEmployerList.isNotEmpty()) {
                    followEmplowercounterTV.text = followedEmployerJobCount?.toString()
                    Log.d("followEmplowercounterTV", "followEmplowercounterTV: $followedEmployerJobCount")
                    var followedCompanyNames = ""
                    followedEmployerList.forEach { item ->
                        followedCompanyNames += item.CompanyName + ","
                    }
                    followedCompanyNameTV.text = followedCompanyNames
                    blankCL.hide()
                    mainLL.show()
                    followedEmployerView.show()
                    loadFirstTime = false
                }
            }

        }
    }

    private fun showJobInvitation() {
        doAsync {
            val jobInvitations = bdjobsDB.jobInvitationDao().getAllJobInvitation()
            uiThread {

                jobInvitationView.hide()
                if (jobInvitations.isNotEmpty()) {
                    var companyNames = ""
                    jobInvitations.forEach { item ->
                        companyNames += item?.companyName + ","
                    }
                    jobInvitedCompanyNameTV.text = companyNames
                    jobInvitationcounterTV.text = jobInvitations.size.toString()
                    blankCL.hide()
                    mainLL.show()
                    jobInvitationView.show()
                    loadFirstTime = false
                }

            }
        }
    }

    private fun showFavouriteSearchFilters() {

        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            uiThread {

                favSearchView.hide()
                if (favouriteSearchFilters.isNotEmpty()) {
                    showAllFavIMGV.setOnClickListener {
                        startActivity<FavouriteSearchBaseActivity>()
                    }
                    favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                    val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters, context = activity)
                    favRV?.adapter = favouriteSearchFilterAdapter
                    blankCL.hide()
                    mainLL.show()
                    favSearchView.show()
                    loadFirstTime = false
                }

            }
        }

    }

    private fun showCertificationInfo() {
        doAsync {
            val b2CCertificationList = bdjobsDB.b2CCertificationDao().getAllB2CCertification()
            uiThread {
                assesmentView.hide()
                if (b2CCertificationList.isNotEmpty()) {
                    var jobRoles = ""
                    b2CCertificationList.forEach { item ->
                        jobRoles += item?.jobRole + ","
                    }
                    jobRolesTV.text = jobRoles
                    certificationCounterTV.text = b2CCertificationList.size.toString()
                    blankCL.hide()
                    mainLL.show()
                    assesmentView.show()
                    loadFirstTime = false
                }
            }
        }
    }
}