package com.bdjobs.app.LoggedInUserLanding.myJobs

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.FollowEmployerListModelClass
import com.bdjobs.app.Employers.FollowedEmployersAdapter
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.sms.BaseActivity
import kotlinx.android.synthetic.main.fragment_followed_employers.*
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class FollowedEmployersFragment : Fragment() {

    private lateinit var bdjobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    private lateinit var isActivityDate: String
    var followedListSize = 0
    private var followedEmployerList: List<FollowEmployerListData>? = null
    private var currentPage = 1
    private var TOTAL_PAGES: Int? = 1
    private var isLoadings = false
    private var isLastPages = false
    private lateinit var bdjobsUserSession: BdjobsUserSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_followed_employers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(requireContext())
        bdjobsUserSession = BdjobsUserSession(requireContext())


        try {
            followedEmployersAdapter = FollowedEmployersAdapter(requireContext())
            followedRV?.adapter = followedEmployersAdapter
            followedRV?.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            followedRV?.layoutManager = layoutManager
            //Log.d("initPag", "called")
            followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

            loadData(1)

            followedRV?.addOnScrollListener(object : PaginationScrollListener((followedRV.layoutManager as LinearLayoutManager?)!!) {

                override val totalPageCount: Int
                    get() = TOTAL_PAGES!!
                override val isLastPage: Boolean
                    get() = isLastPages
                override val isLoading: Boolean
                    get() = isLoadings


                override fun loadMoreItems() {
                    //Log.d("rakib", "called")
                    isLoadings = true
                    currentPage += 1
                    //loadData(currentPage);

                    loadNextPage()
                }
            })


        } catch (e: Exception) {
            logException(e)
            Timber.e("Exception : ${e.localizedMessage}")
        }

        btn_sms_settings?.setOnClickListener {
            startActivity<BaseActivity>("from" to "employer")
        }

    }

    private fun loadNextPage() {

        //Log.d("rakib", "load more $currentPage")

        if (currentPage <= TOTAL_PAGES?:0){
            try {
                ApiServiceJobs.create().getFollowEmployerListLazy(
                    pg = currentPage.toString(),
                    isActivityDate = isActivityDate,
                    userID = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    encoded = Constants.ENCODED_JOBS


                ).enqueue(object : Callback<FollowEmployerListModelClass> {
                    override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                        //Log.d("getFEmployerListLazy", t.message)
                    }

                    override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {

                        try {
                            TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()
                            followedEmployersAdapter?.removeLoadingFooter()
                            isLoadings = false
                            followedEmployersAdapter?.addAll(response.body()?.data as List<FollowEmployerListData>)
                            if (currentPage != TOTAL_PAGES)
                                followedEmployersAdapter?.addLoadingFooter()
                            else
                                isLastPages = true
                        } catch (e: java.lang.Exception) {
                            logException(e)
                        }

                    }

                })

            } catch (e : java.lang.Exception){
                logException(e)
            }
        }

    }

    private fun loadData(currentPage: Int) {

        ApiServiceJobs.create().getFollowEmployerListLazy(
            pg = currentPage.toString(),
            isActivityDate = isActivityDate,
            userID = bdjobsUserSession.userId,
            decodeId = bdjobsUserSession.decodId,
            encoded = Constants.ENCODED_JOBS


        ).enqueue(object : Callback<FollowEmployerListModelClass> {
            override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                //Log.d("getFEmployerListLazy", t.message)
            }

            override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {

                try {


                    followedEmployerList = response.body()?.data

                    followedListSize = response.body()?.common?.totalRecordsFound?.toInt()!!
                    followedEmployersAdapter?.addAll(followedEmployerList!!)

                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()

                    TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()
                    if (currentPage <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                        followedEmployersAdapter?.addLoadingFooter()
                    } else {
                        isLastPages = true
                    }
                } catch (e: Exception) {
                    logException(e)
                }

                try {
                    if (followedEmployerList?.size!! > 0) {
                        followEmployerNoDataLL?.hide()
                        followedRV?.show()
                        //Log.d("totalJobs", "data ase")
                    } else {
                        followEmployerNoDataLL?.show()
                        followedRV?.hide()
                        //Log.d("totalJobs", "zero")
                    }
                } catch (e: Exception) {
                    logException(e)
                }

                try {
                    if (followedListSize > 1) {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employers"
                        favCountTV?.text = Html.fromHtml(styledText)
                    } else {
                        val styledText = "<b><font color='#13A10E'>${followedListSize}</font></b> Followed Employer"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })

    }

}