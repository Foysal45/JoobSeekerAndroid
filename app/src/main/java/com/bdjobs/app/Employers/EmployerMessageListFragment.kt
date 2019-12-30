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
import com.bdjobs.app.API.ModelClasses.EmployerMessageModel
import com.bdjobs.app.API.ModelClasses.MessageDataModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
//import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_employer_message_list.*
//import kotlinx.android.synthetic.main.fragment_employer_message_list.adView
import kotlinx.android.synthetic.main.fragment_followed_employers_list.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerMessageListFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var employersCommunicator: EmployersCommunicator
    private var employerMessageListAdapter: EmployerMessageListAdapter? = null
    private var employerMessageList: ArrayList<MessageDataModel>? = null

    private var PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    var messagelistSize = 0
    var activityDate = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_message_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeViews()
        messageBackIMV?.onClick {

            employersCommunicator.backButtonPressed()

        }


    }

    private fun loadNextPage(activityDate: String) {
        ApiServiceMyBdjobs.create().getEmployerMessageList(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                itemsPerPage = "10",
                pageNumber = pgNo.toString(),
                isActivityDate = activityDate


        ).enqueue(object : Callback<EmployerMessageModel> {
            override fun onFailure(call: Call<EmployerMessageModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<EmployerMessageModel>, response: Response<EmployerMessageModel>) {
                try {

//                    //Log.d("rakib", "total ${employerMessageListAdapter?.itemCount}")
                    val resp_jobs = response.body()
                    val results = response.body()?.data
                    TOTAL_PAGES = resp_jobs?.common?.totalpages


                    employerMessageListAdapter?.removeLoadingFooter()
                    isLoadings = false

                    employerMessageListAdapter?.addAll((results as ArrayList<MessageDataModel>?)!!)

                    employerMessageList?.addAll(results!!.filterNotNull())

                    //Log.d("rakib", "load more ${employerMessageList?.size}")


                    if (pgNo == TOTAL_PAGES!!) {
                        isLastPages = true
                    } else {
                        employerMessageListAdapter?.addLoadingFooter()
                    }


                } catch (e: Exception) {
                    logException(e)
                }

            }

        })
    }

    private fun initializeViews() {
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        employersCommunicator = activity as EmployersCommunicator
        employerMessageListAdapter = EmployerMessageListAdapter(activity)

        activityDate = if (Constants.myBdjobsStatsLastMonth) {
            "1"
        } else {
            "0"
        }

        backIMV?.setOnClickListener {
            employersCommunicator?.backButtonPressed()
        }


        try {
            employerMessageListAdapter = EmployerMessageListAdapter(activity)
            employerMessageRV?.adapter = employerMessageListAdapter
            employerMessageRV?.setHasFixedSize(true)
            employerMessageRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            employerMessageRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()


            if (employersCommunicator?.getEmployerMessageList().isNullOrEmpty()) {
                shimmer_view_container_employerMessage?.show()
                shimmer_view_container_employerMessage?.startShimmerAnimation()
                //Log.d("rakib", "came here first page")
                loadFirstPage(activityDate)
            } else {
                try {
                    //Log.d("rakib", "came here not first page ${messagelistSize}")
                    if (employersCommunicator.getTotalRecords()!! > 1) {
                        val styledText = "<b><font color='#13A10E'>${employersCommunicator?.getTotalRecords().toString()}</font></b> Messages from Employer(s)"
                        messageCountTV?.text = Html.fromHtml(styledText)
                    } else if (employersCommunicator?.getTotalRecords()?.toInt()!! <= 1 || employersCommunicator?.getTotalRecords()?.toInt()!! == null || employersCommunicator?.getTotalRecords().toString() == "0") {
                        val styledText = "<b><font color='#13A10E'>${employersCommunicator?.getTotalRecords().toString()}</font></b> Message from Employer(s)"
                        messageCountTV?.text = Html.fromHtml(styledText)
                    }
                    employerMessageListAdapter?.removeAll()
                    employerMessageListAdapter?.addAll(employersCommunicator?.getEmployerMessageList()!!)
                    employerMessageRV?.layoutManager?.scrollToPosition(employersCommunicator?.getPositionClicked()!!)
                    pgNo = employersCommunicator?.getCurrentPage()!!
//                    TOTAL_PAGES = employersCommunicator?.getTotalPage()!!
//                    isLoadings = employersCommunicator.getIsloading()!!
//                    isLastPages = employersCommunicator.getIsLastPage()!!
//                    messagelistSize = employersCommunicator?.getEmployerMessageList()!!.size
                } catch (e: Exception) {
                }
//                try {
//                    if (messagelistSize > 1) {
//                        val styledText = "<b><font color='#13A10E'>$messagelistSize</font></b> Messages from Employer(s)"
//                        messageCountTV?.text = Html.fromHtml(styledText)
//                    } else if (messagelistSize?.toInt()!! <= 1 || messagelistSize!! == null || messagelistSize.toString() == "0") {
//                        val styledText = "<b><font color='#13A10E'>$messagelistSize</font></b> Message from Employer(s)"
//                        messageCountTV?.text = Html.fromHtml(styledText)
//                    }
//                } catch (e: Exception) {
//                    logException(e)
//                }
            }

            employerMessageRV?.addOnScrollListener(object : PaginationScrollListener((employerMessageRV?.layoutManager as LinearLayoutManager?)!!) {
                override val isLoading: Boolean
                    get() = isLoadings
                override val totalPageCount: Int
                    get() = TOTAL_PAGES!!
                override val isLastPage: Boolean
                    get() = isLastPages

                override fun loadMoreItems() {
                    isLoadings = true
                    pgNo += 1
                    loadNextPage(activityDate)
                }
            })

        } catch (e: Exception) {
            logException(e)
        }

    }


    override fun onResume() {
        super.onResume()
//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
//        Ads.loadAdaptiveBanner(activity.applicationContext,adView)

        //Log.d("rakib", "${employersCommunicator?.getPositionClicked()}")


    }

    private fun loadFirstPage(activityDate: String) {

//        PAGE_START = 1
//        TOTAL_PAGES = null
//        pgNo = PAGE_START
//        isLastPages = false
//        isLoadings = false

        try {
//            employerMessageRV?.hide()
//            messageCountTV?.hide()
//            shimmer_view_container_employerMessage?.show()
//            shimmer_view_container_employerMessage?.startShimmerAnimation()


            ApiServiceMyBdjobs.create().getEmployerMessageList(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    itemsPerPage = "10",
                    pageNumber = pgNo.toString(),
                    isActivityDate = activityDate
            ).enqueue(object : Callback<EmployerMessageModel> {
                override fun onFailure(call: Call<EmployerMessageModel>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        shimmer_view_container_employerMessage?.hide()
                        shimmer_view_container_employerMessage?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<EmployerMessageModel>, response: Response<EmployerMessageModel>) {
                    try {
                        d("fdjkghfjkg onResponse called")
                        shimmer_view_container_employerMessage?.hide()
                        shimmer_view_container_employerMessage?.stopShimmerAnimation()

                        employerMessageList = response?.body()?.data as ArrayList<MessageDataModel>
                        var totalRecords = response.body()?.common?.totalMessage.toString()
                        messagelistSize = response?.body()?.common?.totalpages?.toInt()!!
                        TOTAL_PAGES = response.body()?.common?.totalpages

                        employersCommunicator?.setTotalRecords(totalRecords.toInt())

                        if (!response.body()?.data.isNullOrEmpty()) {

                            d("fdjkghfjkg isNullOrEmpty called")
                            employerMessageRV?.show()

                            val value = response.body()?.data
                            try {

                                employerMessageListAdapter?.addAll(employerMessageList!!)
                                //Log.d("rakib", "total ${employerMessageList?.size!!}")
                            } catch (e: Exception) {

                            }

                            if (pgNo == TOTAL_PAGES!!) {
                                isLastPages = true
                            } else {
                                employerMessageListAdapter?.addLoadingFooter()
                            }


                        } else {

                            totalRecords = "0"
                        }

//                        //Log.d("tot", "total = $totalRecords")
//                        val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Messages from Employer(s)"
//                        messageCountTV.text = Html.fromHtml(styledText)

                        if (totalRecords?.toInt() > 1) {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Messages from Employer(s)"
                            messageCountTV?.text = Html.fromHtml(styledText)
                        } else if (totalRecords?.toInt()!! <= 1 || totalRecords.toInt()!! == null || totalRecords.toString() == "0") {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Message from Employer(s)"
                            messageCountTV?.text = Html.fromHtml(styledText)
                        }


                    } catch (e: Exception) {
                        logException(e)
                    }

                    messageCountTV?.show()
                    shimmer_view_container_employerMessage?.hide()
                    shimmer_view_container_employerMessage?.stopShimmerAnimation()
                }

            })
        } catch (e: Exception) {
            logException(e)
        }
    }

    override fun onPause() {
        super.onPause()
        employersCommunicator.setEmployerMessageList(employerMessageList)
        employersCommunicator.setCurrentPage(pgNo)
        employersCommunicator.setTotalPage(TOTAL_PAGES)
        employersCommunicator.setIsloading(isLoadings)
        employersCommunicator.setIsLastPage(isLastPages)
        //Log.d("rakib", "${employersCommunicator.getPositionClicked()}")
    }


}
