package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.Databases.Internal.JobInvitation
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.loadFirstTime
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_interview_invitation_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)

        nameTV.text = bdjobsUserSession.fullName
        emailTV.text = bdjobsUserSession.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)

        if (loadFirstTime) {
            Timer("loadHomeData", false).schedule(1500) {
                getData()
            }
        } else {
            getData()
        }
    }


    private fun getData() {
        Log.i("DatabaseUpdateJob", "Home Fragment Start: ${Calendar.getInstance().time}")
        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val jobInvitations = bdjobsDB.jobInvitationDao().getAllJobInvitation()
            val followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {
                setData(favouriteSearchFilters, followedEmployerList, followedEmployerJobCount, jobInvitations)
            }
        }
    }

    private fun setData(favouriteSearchFilters: List<FavouriteSearch>, followedEmployerList: List<FollowedEmployer>, followedEmployerJobCount: Int, jobInvitations: List<JobInvitation>) {
        if (followedEmployerList.isEmpty() && favouriteSearchFilters.isEmpty() && jobInvitations.isEmpty()) {
            blankCL.show()
            mainLL.hide()
        } else {
            blankCL.hide()
            mainLL.show()

            favSearchView.hide()
            if (favouriteSearchFilters.isNotEmpty()) {
                showAllFavIMGV.setOnClickListener {
                    startActivity<FavouriteSearchBaseActivity>()
                }
                favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters, context = activity)
                favRV?.adapter = favouriteSearchFilterAdapter
                favSearchView.show()
            }

            jobInvitationView.hide()
            if (jobInvitations.isNotEmpty()) {
                var companyNames = ""
                jobInvitations.forEach { item ->
                    companyNames += item?.companyName + ","
                }
                jobInvitedCompanyNameTV.text = companyNames
                jobInvitationcounterTV.text = jobInvitations.size.toString()
                jobInvitationView.show()
            }

            followedEmployerView.hide()
            if (followedEmployerList.isNotEmpty()) {
                followEmplowercounterTV.text = followedEmployerJobCount?.toString()
                Log.d("followEmplowercounterTV", "followEmplowercounterTV: $followedEmployerJobCount")
                var followedCompanyNames = ""
                followedEmployerList.forEach { item ->
                    followedCompanyNames += item.CompanyName + ","
                }
                followedCompanyNameTV.text = followedCompanyNames
                followedEmployerView.show()
            }
            loadFirstTime=false
        }
    }

}