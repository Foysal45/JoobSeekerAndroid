package com.bdjobs.app.AppliedJobs


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
import com.bdjobs.app.API.ModelClasses.AppliedJobModel
import com.bdjobs.app.API.ModelClasses.AppliedJobModelActivity
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_applied_jobs.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        initializeViews()
        backIMV?.setOnClickListener {
            appliedJobsCommunicator.backButtonPressed()
        }
    }


    private fun initializeViews() {
        time = appliedJobsCommunicator.getTime()
        appliedJobsAdapter = AppliedJobsAdapter(activity)
        appliedJobsRV!!.adapter = appliedJobsAdapter
        appliedJobsRV!!.setHasFixedSize(true)
        appliedJobsRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        Log.d("initPag", "called")
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

        loadFirstPage(time)

    }


    override fun onResume() {
        super.onResume()

        if(appliedJobsCommunicator.getFrom().equalIgnoreCase("employerInteraction")){
            bdjobsUsersession = BdjobsUserSession(activity)

            time = appliedJobsCommunicator.getTime()
            initializeViews()
            backIMV?.setOnClickListener {
                appliedJobsCommunicator.backButtonPressed()
            }
        }

    }

    private fun loadFirstPage(activityDate: String) {
//
        try {
            appliedJobsRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_appliedJobList?.show()
            shimmer_view_container_appliedJobList?.startShimmerAnimation()


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
                        shimmer_view_container_appliedJobList?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<AppliedJobModel>, response: Response<AppliedJobModel>) {
                    /* shimmer_view_container_appliedJobList?.hide()
                     shimmer_view_container_appliedJobList?.stopShimmerAnimation()*/
                    var totalRecords = response.body()?.common?.totalNumberOfApplication
                    Log.d("totalrecords", "totalrecords  = $totalRecords")

                    if (totalRecords != null) {
                        //   toast("came")
                    } else {

                        //    toast("came1")
                        totalRecords = "0"
                        favCountTV?.show()
                        val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Job Applied"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                    try {
                        Log.d("callAppliURl", "url: ${call?.request()} and ")
                        TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                        //   TOTAL_PAGES = 5

                        jobsAppliedSize = totalRecords?.toInt()!!





                        if (!response?.body()?.data.isNullOrEmpty()) {
                            appliedJobsRV!!.visibility = View.VISIBLE
                            var value = response.body()?.data
                            appliedJobsAdapter?.removeAll()
                            appliedJobsAdapter?.addAll(value as List<AppliedJobModelData>)
                            appliedData?.addAll(response.body()?.data as ArrayList<AppliedJobModelData>)
                            appliedJobsAdapter?.addAllActivity(response.body()?.activity as List<AppliedJobModelActivity>)

                            experienceList?.addAll(response.body()?.exprience as List<AppliedJobModelExprience>)
                            Log.d("callAppliURlex", "size = ${value?.size}")
                            appliedJobsCommunicator.setexperienceList(experienceList!!)
                            jobsAppliedSize = totalRecords?.toInt()!!

                            if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                                Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                                appliedJobsAdapter?.addLoadingFooter()
                            } else {
                                Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                                isLastPages = true
                            }

                        } else {
                            //toast("came here")
                            totalRecords = "0"
                        }

                        Log.d("tot", "total = $totalRecords")
                        /* val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs Applied"
                         favCountTV.text = Html.fromHtml(styledText)*/

                        if (totalRecords?.toInt()!! > 1) {
                            val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs Applied"
                            favCountTV?.text = Html.fromHtml(styledText)
                        } else  {
                            val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Job Applied"
                            favCountTV?.text = Html.fromHtml(styledText)
                        }


                    } catch (e: Exception) {
                        logException(e)
                    }
                    appliedJobsRV?.show()
                    favCountTV?.show()
                    shimmer_view_container_appliedJobList?.hide()
                    shimmer_view_container_appliedJobList?.stopShimmerAnimation()
                }

            })
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun addExp(r: AppliedJobModelExprience) {
        experienceList?.add(r)
        // notifyItemInserted(appliedJobsLists!!.size - 1)
    }


    fun addAll(moveResults: List<AppliedJobModelExprience>) {
        for (result in moveResults!!) {
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

                override fun onResponse(call: Call<AppliedJobModel>, response: Response<AppliedJobModel>) {

                    try {
                        Log.d("callAppliURl", "url: ${call?.request()} and $pgNo")
                        Log.d("callAppliURl", response.body()?.data.toString())
                        TOTAL_PAGES = TOTAL_PAGES?.plus(1)

                        //response.body()?.common?.totalpages?.toInt()
                        appliedJobsAdapter?.removeLoadingFooter()
                        isLoadings = false

                        appliedJobsAdapter?.addAll((response?.body()?.data as List<AppliedJobModelData>?)!!)


                        if (pgNo != TOTAL_PAGES)
                            appliedJobsAdapter?.addLoadingFooter()
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

    fun scrollToUndoPosition(position:Int){
        appliedJobsRV?.scrollToPosition(position)
        jobsAppliedSize++
        Log.d("jobiiii", "scrollToUndoPosition = ${jobsAppliedSize}")
        if (jobsAppliedSize> 1) {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Jobs Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Job Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        }

    }

    fun decrementCounter(){
        jobsAppliedSize--
        Log.d("jobiiii", "decrementCounter = ${jobsAppliedSize}")
        if (jobsAppliedSize> 1) {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Jobs Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$jobsAppliedSize</font></b> Job Applied"
            favCountTV?.text = Html.fromHtml(styledText)
        }
    }


}
