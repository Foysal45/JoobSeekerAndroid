package com.bdjobs.app.Employers

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmpVwdResume
import com.bdjobs.app.API.ModelClasses.EmpVwdResumeData
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_employer_viewed_my_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerViewedMyResumeFragment : Fragment() {

    private var PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private lateinit var employerViewedMyResumeAdapter: EmployerViewedMyResumeAdapter
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var employerCommunicator: EmployersCommunicator
    private lateinit var isActivityDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_viewed_my_resume, container, false)
        Log.d("called", "onCreateView")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employerCommunicator = activity as EmployersCommunicator
        isActivityDate = employerCommunicator.getTime()
        Log.d("test", "test" + isActivityDate)
        Log.d("called", "onActivityCreated")

        backIMV?.setOnClickListener {
            employerCommunicator.backButtonPressed()
        }



    }

    override fun onResume() {
        super.onResume()
        initializeViews()
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        Log.d("called", "onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d("called", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("called", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("called", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("called", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("called", "onDetach")
    }


    private fun initializeViews() {
        bdjobsUserSession = BdjobsUserSession(activity)
        employerViewedMyResumeAdapter = EmployerViewedMyResumeAdapter(activity)
        viewedMyResumeRV!!.adapter = employerViewedMyResumeAdapter
        viewedMyResumeRV!!.setHasFixedSize(true)
        viewedMyResumeRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        Log.d("initPag", "called")
        viewedMyResumeRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        viewedMyResumeRV?.addOnScrollListener(object : PaginationScrollListener((viewedMyResumeRV.layoutManager as LinearLayoutManager?)!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(isActivityDate)
            }
        })

        loadFirstPage(isActivityDate)

    }

    private fun loadFirstPage(activityDate: String) {
        PAGE_START = 1
        TOTAL_PAGES = null
        pgNo = PAGE_START
        isLastPages = false
        isLoadings = false

        try {
            viewedMyResumeRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_employerViewedMyList?.show()
            shimmer_view_container_employerViewedMyList?.startShimmerAnimation()


            ApiServiceMyBdjobs.create().getEmpVwdMyResume(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    pageNumber = pgNo.toString(),
                    itemsPerPage = "10",
                    isActivityDate = activityDate,
                    AppsDate = "1"

                    /*    userId = "241028",
                        decodeId = "T8B8Rx",
                        pageNumber = "1",
                        itemsPerPage = "10",
                        isActivityDate = "1",
                        AppsDate = ""*/

            ).enqueue(object : Callback<EmpVwdResume> {
                override fun onFailure(call: Call<EmpVwdResume>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        shimmer_view_container_employerViewedMyList?.hide()
                        shimmer_view_container_employerViewedMyList?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<EmpVwdResume>, response: Response<EmpVwdResume>) {
                    Log.d("popup", "popup-" + bdjobsUserSession.userId!! +
                            "de-" + bdjobsUserSession.decodId!!)

                    Log.d("callAppliURl", "url: ${call?.request()} and ${response.code()}")
                    Log.d("callAppliURl", "url: ${response.body()?.data}")
                    shimmer_view_container_employerViewedMyList?.hide()
                    shimmer_view_container_employerViewedMyList?.stopShimmerAnimation()


                    try {

                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        val totalRecords = response.body()?.common?.totalNumberOfItems
                        if (!response?.body()?.data.isNullOrEmpty()) {
                            resumeViewNoDataLL?.hide()
                            viewedMyResumeRV?.show()
                            //Log.d("oooooo", "url: ${response?.body()?.data} and ")

                            val value = response.body()?.data
                            employerViewedMyResumeAdapter?.removeAll()
                            employerViewedMyResumeAdapter?.addAll(response?.body()?.data as List<EmpVwdResumeData>)

                            if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                                Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                                employerViewedMyResumeAdapter?.addLoadingFooter()
                            } else {
                                Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                                isLastPages = true
                            }

                            Log.d("totalJobs", "totalRecords $totalRecords")




                            if (totalRecords?.toInt()!! > 1) {
                                val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Employers viewed my Resume"
                                favCountTV?.text = Html.fromHtml(styledText)
                            } else if (totalRecords?.toInt()!! <= 1 || (totalRecords.toInt()!! == null)) {
                                val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Employer viewed my Resume"
                                favCountTV?.text = Html.fromHtml(styledText)
                            }


                        } else {


                            resumeViewNoDataLL?.show()
                            viewedMyResumeRV?.hide()
                            Log.d("totalJobs", "zero")


                        }

                        /*   viewedMyResumeRV?.show()*/
                        favCountTV?.show()
                        shimmer_view_container_employerViewedMyList?.hide()
                        shimmer_view_container_employerViewedMyList?.stopShimmerAnimation()

                    } catch (exception: Exception) {
                        Log.d("issue", exception.toString())

                    }
                }

            })
        } catch (e: Exception) {
            logException(e)
        }


    }

    private fun loadNextPage(activityDate: String) {
        try {
            ApiServiceMyBdjobs.create().getEmpVwdMyResume(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    pageNumber = pgNo.toString(),
                    itemsPerPage = "10",
                    isActivityDate = activityDate,
                    AppsDate = ""


            ).enqueue(object : Callback<EmpVwdResume> {
                override fun onFailure(call: Call<EmpVwdResume>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        error("onFailure", t)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<EmpVwdResume>, response: Response<EmpVwdResume>) {

                    try {
                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        employerViewedMyResumeAdapter?.removeLoadingFooter()
                        isLoadings = false

                        employerViewedMyResumeAdapter?.addAll(response?.body()?.data as List<EmpVwdResumeData>)


                        if (pgNo != TOTAL_PAGES)
                            employerViewedMyResumeAdapter?.addLoadingFooter()
                        else {
                            isLastPages = true
                        }

                    } catch (e: Exception) {
                        logException(e)
                    }
                }


            })
        } catch (e: Exception) {
            logException(e)
        }


    }

}
