package com.bdjobs.app.ManageResume


import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.TimesEmailed
import com.bdjobs.app.API.ModelClasses.TimesEmailedData
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_times_emailed_my_resume.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimesEmailedMyResumeFragment : Fragment() {

    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private var bdjobsUserSession: BdjobsUserSession? = null
    private lateinit var isActivityDate: String

    private lateinit var manageCommunicator: ManageResumeCommunicator
    private lateinit var timesEmailedMyResumeAdapter: TimesEmailedMyResumeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_times_emailed_my_resume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        manageCommunicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity!!)

        backIMV.setOnClickListener {
             manageCommunicator.backButtonPressed()
        }

        newEmaiResume.setOnClickListener {
            manageCommunicator.gotoEmailResumeFragment()
        }


    /*    if (Constants.timesEmailedResumeLast) {
            isActivityDate = "0"
            allTV.performClick()
            lastOnClickAction(isActivityDate)
        } else if (!Constants.timesEmailedResumeLast) {
            isActivityDate = "1"
            matchedTV.performClick()
            allOnClickAction(isActivityDate)

        }*/

        Log.d("isActivityDate", "vava = ${Constants.timesEmailedResumeLast}")

        if (!Constants.timesEmailedResumeLast) {
            allTV.performClick()
            isActivityDate = "0"
            allOnClickAction(isActivityDate)

            initPagination()
        }
        else if(Constants.timesEmailedResumeLast){
            matchedTV.performClick()
            isActivityDate = "1"
            lastOnClickAction(isActivityDate)

            initPagination()
        }


    }
    override fun onResume() {
        super.onResume()


        matchedTV.setOnClickListener {
            if (!Constants.timesEmailedResumeLast) {
                isActivityDate = "1"
                lastOnClickAction(isActivityDate)
                Constants.timesEmailedResumeLast = true
            }
            initPagination()
        }

        allTV.setOnClickListener {
            if (Constants.timesEmailedResumeLast) {
                isActivityDate = "0"
                allOnClickAction(isActivityDate)
                Constants.timesEmailedResumeLast = false
            }


            initPagination()
        }

    }

    private fun lastOnClickAction(isActivityDate : String) {
        // lastSelected()
        lastSelected()
        loadFirstPage(isActivityDate)
        Constants.timesEmailedResumeLast = true
    }

    private fun allOnClickAction(isActivityDate : String) {
        // allSelected()
        allSelected()
        loadFirstPage(isActivityDate)
        Constants.timesEmailedResumeLast = false

    }

    private fun lastSelected() {
        matchedTV.setTextColor(Color.parseColor("#FFFFFF"))
        matchedTV.setBackgroundResource(R.drawable.success_active_bg)
        allTV.setTextColor(Color.parseColor("#424242"))
        allTV.setBackgroundResource(R.drawable.pending_inactive_bg)
//        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 0
//        verifyStatus = "1"

    }

    private fun allSelected() {
        allTV.setTextColor(Color.parseColor("#FFFFFF"))
        allTV.setBackgroundResource(R.drawable.pending_active_bg)
        matchedTV.setTextColor(Color.parseColor("#424242"))
        matchedTV.setBackgroundResource(R.drawable.success_inactive_bg)
//        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 1
//        verifyStatus = "0"
    }

    private fun initPagination() {
        timesEmailedMyResumeAdapter = TimesEmailedMyResumeAdapter(activity!!)
        emailedResumeRV!!.setHasFixedSize(true)
        emailedResumeRV!!.adapter = timesEmailedMyResumeAdapter
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        emailedResumeRV!!.layoutManager = layoutManager as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        emailedResumeRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        emailedResumeRV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(isActivityDate!!)
            }
        })
        loadFirstPage(isActivityDate!!)

    }

    private fun loadFirstPage(isActivityDate: String) {
        emailedResumeRV?.hide()
        shimmer_view_container_emailedResumeList?.show()
        shimmer_view_container_emailedResumeList?.startShimmerAnimation()
        numberTV.text = "0"
        ApiServiceMyBdjobs.create().emailedMyResume(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "20",
                isActivityDate = isActivityDate
        ).enqueue(object : Callback<TimesEmailed> {
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {
                Log.d("isActivityDate", "vava = $isActivityDate")
                try {
                    TOTAL_PAGES = response?.body()?.common?.totalNumberOfPage?.toInt()
                    var totalEmailRecords = response?.body()?.common?.totalNumberOfEmail
                    if (!response?.body()?.data.isNullOrEmpty()) {
                        emailedResumeRV!!.visibility = View.VISIBLE
                        timesEmailedMyResumeAdapter?.removeAll()
                        timesEmailedMyResumeAdapter?.addAll((response?.body()?.data as List<TimesEmailedData>?)!!)

                        if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                            Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                            timesEmailedMyResumeAdapter?.addLoadingFooter()
                        } else {
                            Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                            isLastPages = true
                        }

                    }
                    val styledText = "<b><font color='#13A10E'>$totalEmailRecords</font></b> "
                    numberTV?.text = Html.fromHtml(styledText)

                    emailedResumeRV?.show()
                    numberTV?.show()
                    shimmer_view_container_emailedResumeList?.hide()
                    shimmer_view_container_emailedResumeList?.stopShimmerAnimation()
                } catch (e: Exception) {

                }
            }

        })
    }

    private fun loadNextPage(isActivityDate: String) {
        ApiServiceMyBdjobs.create().emailedMyResume(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "20",
                isActivityDate = isActivityDate
        ).enqueue(object : Callback<TimesEmailed> {
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {
                try {

                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    timesEmailedMyResumeAdapter?.removeLoadingFooter()
                    isLoadings = false

                    timesEmailedMyResumeAdapter?.addAll((response?.body()?.data as List<TimesEmailedData>?)!!)


                    if (pgNo != TOTAL_PAGES)
                        timesEmailedMyResumeAdapter?.addLoadingFooter()
                    else {
                        isLastPages = true
                    }


                } catch (e: Exception) {

                }
            }

        })

    }


}
