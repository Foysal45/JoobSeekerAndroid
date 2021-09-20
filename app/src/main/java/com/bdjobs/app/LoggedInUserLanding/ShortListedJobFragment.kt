package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.Jobs.JoblistAdapter
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_shortlisted_job_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.selector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ShortListedJobFragment : Fragment() {
    lateinit var bdJobsDB: BdjobsDB
    lateinit var bdJobsUserSession: BdjobsUserSession
    lateinit var jobListAdapter: JoblistAdapter
    lateinit var homeCommunicator: HomeCommunicator
    private var currentPage = 1
    private var totalPages: Int? = null
    private var isLoadings = false
    private var isLastPages = false
    var totalRecordsFound = 0
    private var layoutManager: RecyclerView.LayoutManager? = null
    var favListSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shortlisted_job_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdJobsDB = BdjobsDB.getInstance(requireContext())
        bdJobsUserSession = BdjobsUserSession(requireContext())
        homeCommunicator = requireActivity() as HomeCommunicator
        profilePicIMGV?.loadCircularImageFromUrl(bdJobsUserSession.userPicUrl)
        searchIMGV?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        profilePicIMGV?.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }

        filterTV?.setOnClickListener {
            val deadline = arrayOf("Today", "Tomorrow", "Next 2 days", "Next 3 days", "Next 4 days")
            selector("Jobs expire in", deadline.toList()) { _, i ->
                showShortListFilterList(deadline[i])
            }
        }
        crossBTN?.setOnClickListener {
            showShortListFilterList("")
        }

        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }

        messageIMGV?.setOnClickListener {
            homeCommunicator.goToMessages()
        }

        btn_job_list.setOnClickListener {
            homeCommunicator.gotoAllJobSearch()
        }


    }

    override fun onResume() {
        super.onResume()
        showNotificationCount()
        showMessageCount()
        val shortListFilter = homeCommunicator.getShortListFilter()
        showShortListFilterList(shortListFilter)
    }

    @SuppressLint("SetTextI18n")
    fun updateMessageView(count: Int?) {
        if (count!! > 0) {
            messageCountTV?.show()
            if (count <= 99)
                messageCountTV?.text = "$count"
            else
                messageCountTV?.text = "99+"
        } else {
            messageCountTV?.hide()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMessageCount() {
        try {

            doAsync {
                bdJobsUserSession = BdjobsUserSession(requireContext())
                val count = bdJobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdJobsUserSession.updateMessageCount(count)
            }

            if (bdJobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdJobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdJobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showNotificationCount() {
        try {
            bdJobsUserSession = BdjobsUserSession(requireContext())
            if (bdJobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdJobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdJobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    private fun showShortListFilterList(shortListFilter: String) {
        filterTV.text = shortListFilter
        homeCommunicator.setShortListFilter(shortListFilter)
        when (shortListFilter) {
            "" -> {
                crossBTN.hide()
                getShortListedJobsByDeadline("")
            }
            "Today" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("1")
            }
            "Tomorrow" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("2")
            }
            "Next 2 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("3")
            }
            "Next 3 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("4")
            }
            "Next 4 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("5")
            }
        }
    }

    private fun getShortListedJobsByDeadline(deadline: String) {
        val styledText = "<b><font color='#13A10E'>0</font></b> Shortlisted job"
        jobCountTV?.text = Html.fromHtml(styledText)
        currentPage = 1
        totalPages = null
        isLoadings = false
        isLastPages = false
        shortListRV?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        shortListRV?.layoutManager = layoutManager
        jobListAdapter = JoblistAdapter(requireContext())
        shortListRV?.adapter = jobListAdapter

        shortListRV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!! as LinearLayoutManager) {

            override val totalPageCount: Int
                get() = totalPages!!
            override val isLastPage: Boolean
                get() = isLastPages
            override var isLoading: Boolean = false
                get() = isLoadings

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                loadNextPage(
                        deadline = deadline,
                        pageNumber = currentPage,
                        rpp = "10"
                )
            }
        })

        loadFirstPageList(
                deadline = deadline,
                pageNumber = currentPage,
                rpp = "10"
        )

    }

    private fun loadFirstPageList(deadline: String, rpp: String, pageNumber: Int) {
        noDataLL?.hide()
        shortListRV.hide()
        jobCountTV.hide()
        filterTV.hide()
        crossBTN.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmer()

        val call = ApiServiceJobs.create().getStoreJobList(
                p_id = bdJobsUserSession.userId,
                encoded = Constants.ENCODED_JOBS,
                deadline = deadline,
                rpp = rpp,
                pg = pageNumber
        )
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    if (response.isSuccessful) {
                        shimmer_view_container_JobList?.hide()
                        shimmer_view_container_JobList?.stopShimmer()
                        jobCountTV.show()
                        filterTV.show()
                        crossBTN.show()

                        val jobResponse = response.body()
                        val totalJobs = jobResponse?.common?.totalRecordsFound

                        if (totalJobs!! > 0) {
                            noDataLL?.hide()
                            shortListRV?.show()
                            //Log.d("totalJobs", "data ase")
                        } else {
                            noDataLL?.show()
                            shortListRV?.hide()
                            //Log.d("totalJobs", "zero")
                        }

                        totalPages = jobResponse.common.totalpages


                        //communicator.totalJobCount(jobResponse?.common?.totalRecordsFound)
                        val results = response.body()?.data

                        if (!results.isNullOrEmpty()) {
                            jobListAdapter.addAll(results)
                        }

                        if (currentPage >= totalPages!!) {
                            isLastPages = true
                        } else {
                            jobListAdapter.addLoadingFooter()
                        }


                        if (totalJobs> 1) {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Shortlisted jobs"
                            jobCountTV?.text = Html.fromHtml(styledText)
                        } else {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Shortlisted job"
                            jobCountTV?.text = Html.fromHtml(styledText)
                        }


                        totalRecordsFound = jobResponse.common.totalRecordsFound
                        favListSize = totalRecordsFound
                        Timber.d("Fav list size: $totalRecordsFound")
                    } else {
                        Timber.d("Unsuccessful Response")
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable) {
                //Log.d("TAG", "not successful!! onFail")
                error("onFailure", t)
            }
        })
    }

    private fun loadNextPage(deadline: String, rpp: String, pageNumber: Int) {
        //Log.d("ArrayTest", " loadNextPage called")

        val call = ApiServiceJobs.create().getStoreJobList(
                p_id = bdJobsUserSession.userId,
                encoded = Constants.ENCODED_JOBS,
                deadline = deadline,
                rpp = rpp,
                pg = pageNumber
        )
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    if (response.isSuccessful) {

                        try {
                            val respJobs = response.body()
                            totalPages = respJobs?.common?.totalpages
                            jobListAdapter.removeLoadingFooter()
                            isLoadings = false

                            val results = response.body()?.data
                            jobListAdapter.addAll(results as List<JobListModelData>)

                            if (currentPage >= totalPages!!) {
                                isLastPages = true
                            } else {
                                jobListAdapter.addLoadingFooter()
                            }

                            /*communicator.setIsLoading(isLoadings)
                            communicator.setLastPasge(isLastPages)
                            communicator.setTotalJob(resp_jobs?.common!!.totalRecordsFound!!.toInt())*/



                            //totalRecordsFound = resp_jobs?.common?.totalRecordsFound!!
                            //favListSize = totalRecordsFound

                            /*if (totalRecordsFound.toInt() > 1) {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Shortlisted jobs"
                                jobCountTV?.text = Html.fromHtml(styledText)
                            } else {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Shortlisted job"
                                jobCountTV?.text = Html.fromHtml(styledText)
                            }*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Timber.d("Unsuccessful response")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {
                //Log.d("TAG", "not successful!! onFail")
            }
        })
    }


    fun scrollToUndoPosition(position: Int) {
        shortListRV?.scrollToPosition(position)
        favListSize++
        if (favListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }
    }

    fun decrementCounter() {
        favListSize--
        Timber.d("Decrement: $favListSize")
        if (favListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }
    }


    @SuppressLint("SetTextI18n")
    fun updateNotificationView(count: Int?) {
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }

    }

}