package com.bdjobs.app.AppliedJobs


import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobModel
import com.bdjobs.app.API.ModelClasses.AppliedJobModelActivity
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import kotlinx.android.synthetic.main.fragment_applied_jobs.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class AppliedJobsFragment : Fragment() {

    private lateinit var bdjobsUsersession: BdjobsUserSession
    private var appliedJobsAdapter: AppliedJobsAdapter? = null
    private var experienceList: ArrayList<AppliedJobModelExprience>? = ArrayList()
    private var appliedData: ArrayList<AppliedJobModelData>? = ArrayList()
    val jobList: MutableList<AppliedJobModelData> = java.util.ArrayList()
    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private lateinit var appliedJobsCommunicator: AppliedJobsCommunicator
    private var time: String = ""
    var jobsAppliedSize = 0
    var daysAvailable = 30
    var jobApplyLimit = 50
    var availableJobs = 30

    var totalContacted = 0
    var totalNotContacted = 0
    var totalHired = 0

    lateinit var messageValidDate: Date
    lateinit var currentDate: Date

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applied_jobs, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        bdjobsUsersession = BdjobsUserSession(activity)
        appliedJobsCommunicator = activity as AppliedJobsCommunicator
        time = appliedJobsCommunicator.getTime()
        //Log.d("rakib", time)
        initializeViews()
        backIMV?.setOnClickListener {
            appliedJobsCommunicator.backButtonPressed()
        }

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)

    }


    private fun initializeViews() {

        var calendar = Calendar.getInstance()
        daysAvailable = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH)

        time = appliedJobsCommunicator.getTime()
        appliedJobsAdapter = AppliedJobsAdapter(activity)
        appliedJobsRV!!.adapter = appliedJobsAdapter
        appliedJobsRV!!.setHasFixedSize(true)
        appliedJobsRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        //Log.d("initPag", "called")
        appliedJobsRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        appliedJobsRV?.addOnScrollListener(object : PaginationScrollListener((appliedJobsRV.layoutManager as LinearLayoutManager?)!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(time)
            }
        })

        jobApplyLimit = bdjobsUsersession.jobApplyLimit!!.toInt()

//        //Log.d("rakib" ,"onres ${bdjobsUsersession.availableJobsCount}")

        loadFirstPage(time)

    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        if (appliedJobsCommunicator.getFrom().equalIgnoreCase("employerInteraction")) {
            bdjobsUsersession = BdjobsUserSession(activity)

            time = appliedJobsCommunicator.getTime()
            initializeViews()
            backIMV?.setOnClickListener {
                appliedJobsCommunicator.backButtonPressed()
            }
        }

        textView10.text = "You didn't apply for any job yet."

    }

    private fun loadFirstPage(activityDate: String) {

        Timber.d("Loading first page: $activityDate")
//
        try {
            appliedJobsRV?.hide()
            favCountTV?.hide()
            availableJobsCountTV?.hide()
            daysRemainingCountTV?.hide()

            shimmer_view_container_appliedJobList?.show()
            shimmer_view_container_appliedJobList?.startShimmer()


            ApiServiceMyBdjobs.create().getAppliedJobs(
                    userId = bdjobsUsersession.userId,
                    decodeId = bdjobsUsersession.decodId,
                    isActivityDate = activityDate,
                    pageNumber = pgNo.toString(),
                    itemsPerPage = "20"
            ).enqueue(object : Callback<AppliedJobModel> {
                override fun onFailure(call: Call<AppliedJobModel>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        shimmer_view_container_appliedJobList?.hide()
                        shimmer_view_container_appliedJobList?.stopShimmer()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<AppliedJobModel>, response: Response<AppliedJobModel>) {
                    /* shimmer_view_container_appliedJobList?.hide()
                     shimmer_view_container_appliedJobList?.stopShimmerAnimation()*/

                    try {
                        var totalRecords = response.body()?.common?.totalNumberOfApplication
                        //Log.d("totalrecords", "totalrecords  = $totalRecords")

                        if (totalRecords != null) {
                            //   toast("came")
                        } else {
                            totalRecords = "0"
                            favCountTV?.show()
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Job Applied"
                            favCountTV?.text = Html.fromHtml(styledText)

                        }
                            if (appliedJobsCommunicator.getTime() == "1") {
                                Timber.d("I am here at 1")
                                availableJobsCountTV?.show()

                                daysRemainingCountTV?.show()

                                if (daysAvailable > 1) {
                                    val text = "<b><font color='#2F4858'>${daysAvailable}</font></b> Days remaining"
                                    daysRemainingCountTV?.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                } else {
                                    val text = "<b><font color='#2F4858'>${daysAvailable}</font></b> Day remaining"
                                    daysRemainingCountTV?.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }

                                availableJobs = jobApplyLimit - totalRecords.toInt()

                                Timber.d("Available Jobs: $availableJobs")

                                if (availableJobs > 1) {
                                    val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available jobs"
                                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                } else {
                                    val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available job"
                                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }
                            } else {

                                Timber.d("I am here at 2")
                                availableJobsCountTV?.hide()

                                daysRemainingCountTV?.hide()
                            }




                        //Log.d("callAppliURl", "url: ${call?.request()} and ")
                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        //   TOTAL_PAGES = 5

                        jobsAppliedSize = totalRecords.toInt()


                        if (!response.body()?.data.isNullOrEmpty()) {

                            status_card_ll?.show()
                            job_status_ll?.show()


                            Constants.totalContacted = response.body()?.activity?.get(0)?.totalContacted!!.toInt()
                            Constants.totalNotContacted = response.body()?.activity?.get(0)?.totalNotContacted!!.toInt()
                            Constants.totalHired = response.body()?.activity?.get(0)?.totalHired!!.toInt()

                            not_contacted_count_tv?.text = "${Constants.totalNotContacted}"
                            contacted_count_tv?.text = "${Constants.totalContacted}"
                            hired_count_tv?.text = "${Constants.totalHired}"

                            appliedJobsRV?.show()
                            var value = response.body()?.data
                            appliedJobsAdapter?.removeAll()
                            appliedJobsAdapter?.addAll(value as List<AppliedJobModelData>)
                            appliedData?.addAll(response.body()?.data as ArrayList<AppliedJobModelData>)
                            appliedJobsAdapter?.addAllActivity(response.body()?.activity as List<AppliedJobModelActivity>)

                            experienceList?.addAll(response.body()?.exprience as List<AppliedJobModelExprience>)
                            //Log.d("callAppliURlex", "size = ${value?.size}")
                            appliedJobsCommunicator.setexperienceList(experienceList!!)
                            jobsAppliedSize = totalRecords.toInt()


                            if (pgNo == TOTAL_PAGES!!) {
                                isLastPages = true
                            } else {
                                appliedJobsAdapter?.addLoadingFooter()
                            }


                        } else {
                            //toast("came here")
                            totalRecords = "0"

                            appliedJobsNoDataLL?.show()
                            job_status_ll.hide()
                            appliedJobsRV?.hide()
                            //Log.d("totalJobs", "zero")

                        }

                        //Log.d("tot", "total = $totalRecords")
                        /* val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs Applied"
                         favCountTV.text = Html.fromHtml(styledText)*/

                        if (totalRecords.toInt() > 1) {
                            val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs applied"
                            favCountTV?.text = Html.fromHtml(styledText)
                        } else {
                            val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Job applied"
                            favCountTV?.text = Html.fromHtml(styledText)
                        }

                        try {
                            if (appliedJobsCommunicator.getTime() == "1") {
                                daysRemainingCountTV?.show()
                                if (daysAvailable > 1) {
                                    val text = "<b><font color='#2F4858'>${daysAvailable}</font></b> Days remaining"
                                    daysRemainingCountTV?.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                } else {
                                    val text = "<b><font color='#2F4858'>${daysAvailable}</font></b> Day remaining"
                                    daysRemainingCountTV?.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }


                                //                            //Log.d("rakib", "load $availableJobs")
                                availableJobsCountTV?.show()

                                availableJobs = jobApplyLimit - totalRecords.toInt()

                                if (availableJobs>50) {
                                    daysRemainingCountTV?.hide()
                                } else {
                                    daysRemainingCountTV?.show()
                                }

                                if (availableJobs > 1) {
                                    val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available jobs"
                                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                } else if (availableJobs < 0) {
                                    val availableJobsText = "<b><font color='#B740AD'>0</font></b> Available job"
                                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                } else {
                                    val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available job"
                                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }
                            } else {
                                daysRemainingCountTV?.hide()
                                availableJobsCountTV?.hide()
                            }
                        } catch (e: Exception) {
                        }


                    } catch (e: Exception) {
                        logException(e)
                    }
                    appliedJobsRV?.show()
                    favCountTV?.show()
                    availableJobsCountTV?.show()
                    if (availableJobs>50) {
                        Timber.d("Available job is greater than 50")
                        daysRemainingCountTV?.visibility = View.GONE
                    } else {
                        Timber.d("Available job is lesser than 50")
                        daysRemainingCountTV?.visibility = View.VISIBLE
                    }
                    shimmer_view_container_appliedJobList?.hide()
                    shimmer_view_container_appliedJobList?.stopShimmer()
                }

            })
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun addExp(r: AppliedJobModelExprience) {
        experienceList?.add(r)
        // notifyItemInserted(appliedJobsLists!!.size - 1)
    }


    fun addAll(moveResults: List<AppliedJobModelExprience>) {
        for (result in moveResults) {
            addExp(result)
        }
    }

    private fun loadNextPage(activityDate: String) {
        try {
            ApiServiceMyBdjobs.create().getAppliedJobs(
                    userId = bdjobsUsersession.userId,
                    decodeId = bdjobsUsersession.decodId,
                    isActivityDate = activityDate,
                    pageNumber = pgNo.toString(),
                    itemsPerPage = "20"
            ).enqueue(object : Callback<AppliedJobModel> {
                override fun onFailure(call: Call<AppliedJobModel>, t: Throwable) {
                    activity?.toast("${t.message}")
                }
                //------------------------------

                override fun onResponse(call: Call<AppliedJobModel>, response: Response<AppliedJobModel>) {

                    try {
                        //Log.d("callAppliURl", "url: ${call?.request()} and $pgNo total= ${TOTAL_PAGES}")
                        //Log.d("callAppliURl", response.body()?.data.toString())
                        //TOTAL_PAGES = TOTAL_PAGES?.plus(1)

                        //response.body()?.common?.totalpages?.toInt()
                        appliedJobsAdapter?.removeLoadingFooter()
                        isLoadings = false

                        appliedJobsAdapter?.addAll((response.body()?.data as List<AppliedJobModelData>?)!!)


                        if (pgNo < TOTAL_PAGES!!)
                            appliedJobsAdapter?.addLoadingFooter()
                        else {
                            isLastPages = true
                        }
                        /*     if (pgNo == TOTAL_PAGES!!) {
                                 isLastPages = true
                             } else {
                                 appliedJobsAdapter?.addLoadingFooter()
                             }*/

                    } catch (e: Exception) {
                        logException(e)
                    }

                }

            })
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun scrollToUndoPosition(position: Int) {
        appliedJobsRV?.scrollToPosition(position)
        jobsAppliedSize++
        //Log.d("jobiiii", "scrollToUndoPosition = ${jobsAppliedSize}")
        if (jobsAppliedSize > 1) {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Jobs Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Job Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        }


    }

    fun decrementCounter() {
        jobsAppliedSize--
        //Log.d("jobiiii", "decrementCounter = ${jobsAppliedSize}")
        not_contacted_count_tv?.text = "${Constants.totalNotContacted}"
        contacted_count_tv?.text = "${Constants.totalContacted}"
        hired_count_tv?.text = "${Constants.totalHired}"

        if (jobsAppliedSize > 1) {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Jobs Applied"
            favCountTV?.text = Html.fromHtml(styledText)



        } else {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Job Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        }
    }

    fun incrementAvailableJobCount() {
        try {
            availableJobs = jobApplyLimit - jobsAppliedSize

            if (availableJobs>50) {
                daysRemainingCountTV?.hide()
            } else {
                daysRemainingCountTV?.show()
            }

            if (appliedJobsCommunicator.getTime() == "1") {
                if (availableJobs > 1) {
                    val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available jobs"
                    availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } else {
                    if (availableJobs == 1) {
                        val availableJobsText = "<b><font color='#B740AD'>${availableJobs}</font></b> Available jobs"
                        availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        val availableJobsText = "<b><font color='#B740AD'>0</font></b> Available job"
                        availableJobsCountTV?.text = HtmlCompat.fromHtml(availableJobsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }
            }
        } catch (e: Exception) {
        }

    }


}
