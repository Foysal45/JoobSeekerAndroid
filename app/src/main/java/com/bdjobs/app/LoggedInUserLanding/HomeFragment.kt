package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.JobInvitationListModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_interview_invitation_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        getData()
        nameTV.text = bdjobsUserSession.fullName
        emailTV.text = bdjobsUserSession.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
    }


    private fun getData() {
        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {
                setData(favouriteSearchFilters, followedEmployerList, followedEmployerJobCount)
            }
        }
    }

    private fun setData(favouriteSearchFilters: List<FavouriteSearch>, followedEmployerList: List<FollowedEmployer>, followedEmployerJobCount: Int) {
        if (followedEmployerList.isEmpty() && favouriteSearchFilters.isEmpty()) {
            blankCL.show()
            mainLL.hide()
        } else {
            blankCL.hide()
            mainLL.show()

            if (favouriteSearchFilters.isEmpty()) {
                favSearchView.hide()
            } else {
                favSearchView.show()
                showAllFavIMGV.setOnClickListener {
                    startActivity<FavouriteSearchBaseActivity>()
                }
                favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters, context = activity)
                favRV?.adapter = favouriteSearchFilterAdapter
            }

            if (followedEmployerList.isEmpty()) {
                followedEmployerView.hide()
            } else {
                followedEmployerView.show()
                followEmplowercounterTV.text = followedEmployerJobCount?.toString()
                Log.d("followEmplowercounterTV", "followEmplowercounterTV: $followedEmployerJobCount")
                var followedCompanyNames = ""
                followedEmployerList.forEach { item ->
                    followedCompanyNames += item.CompanyName + ","
                }
                followedCompanyNameTV.text = followedCompanyNames
            }

            showAssesmentInformation()
            showJobInvitationInformation()


        }
    }

    private fun showJobInvitationInformation() {
        jobInvitationView.hide()
        ApiServiceMyBdjobs.create().getJobInvitationList(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<JobInvitationListModel> {
            override fun onFailure(call: Call<JobInvitationListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobInvitationListModel>, response: Response<JobInvitationListModel>) {
                response.body()?.statuscode?.let { status ->
                    if (status == api_request_result_code_ok) {
                        if (response?.body()?.data?.size!! > 0) {
                            jobInvitationView.show()
                            var companyNames = ""
                            response?.body()?.data?.forEach { item ->
                                companyNames += item?.companyName + ","
                            }
                            jobInvitedCompanyNameTV.text = companyNames
                            jobInvitationcounterTV.text =  response?.body()?.data?.size?.toString()
                        }
                    }
                }
            }

        })

    }

    private fun showAssesmentInformation() {

    }
}