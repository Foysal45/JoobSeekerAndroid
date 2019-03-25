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
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_employer_message_list.*
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
    private var messageData: ArrayList<MessageDataModel>? = ArrayList()
    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private var time: String = ""
    var jobsAppliedSize = 0
    var activityDate = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_message_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        employersCommunicator = activity as EmployersCommunicator


        initializeViews()

        messageBackIMV.onClick {

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
                    Log.d("callAppliURl", "url: ${call?.request()} and $pgNo")
                    Log.d("callAppliURl", response.body()?.data.toString())


                    //response.body()?.common?.totalpages?.toInt()
                    employerMessageListAdapter?.removeLoadingFooter()
                    isLoadings = false

                    employerMessageListAdapter?.addAll((response?.body()?.data as List<MessageDataModel>?)!!)


                    if (pgNo != TOTAL_PAGES)
                        employerMessageListAdapter?.addLoadingFooter()
                    else {
                        isLastPages = true
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })
    }

    private fun initializeViews() {
        bdjobsUserSession = BdjobsUserSession(activity)
        employerMessageListAdapter = EmployerMessageListAdapter(activity)
        activityDate = if(Constants.myBdjobsStatsLastMonth) {
            "1"
        } else {
            "0"
        }
        employerMessageRV!!.adapter = employerMessageListAdapter
        employerMessageRV!!.setHasFixedSize(true)
        employerMessageRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        Log.d("initPag", "called")
        employerMessageRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        employerMessageRV?.addOnScrollListener(object : PaginationScrollListener((employerMessageRV.layoutManager as LinearLayoutManager?)!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(activityDate)
            }
        })


        loadFirstPage(activityDate)

    }



    private fun loadFirstPage(activityDate: String) {

        d("fdjkghfjkg loadFirstPage called")

        try {
            employerMessageRV?.hide()
            messageCountTV?.hide()
            shimmer_view_container_employerMessage?.show()
            shimmer_view_container_employerMessage?.startShimmerAnimation()


            ApiServiceMyBdjobs.create().getEmployerMessageList(bdjobsUserSession.userId, bdjobsUserSession.decodId, "10", pgNo.toString(),activityDate
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

                    d("fdjkghfjkg onResponse called")

                    shimmer_view_container_employerMessage?.hide()
                    shimmer_view_container_employerMessage?.stopShimmerAnimation()
                    var totalRecords = response.body()?.common?.totalMessage.toString()
                    Log.d("totalrecords", "totalrecords  = $totalRecords")

                    messageCountTV?.show()
                        val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Messages"
                    messageCountTV?.text = Html.fromHtml(styledText)

                    try {
                        Log.d("callAppliURl", "url: ${call?.request()} and ")
                        TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()
                        //   TOTAL_PAGES = 5

                        jobsAppliedSize = totalRecords?.toInt()!!


                        if (!response.body()?.data.isNullOrEmpty()) {

                            d("fdjkghfjkg isNullOrEmpty called")
                            employerMessageRV?.show()

                            val value = response.body()?.data
                            employerMessageListAdapter?.removeAll()
                            employerMessageListAdapter?.addAll(value as List<MessageDataModel>)
                            /*messageData?.addAll(response.body()?.data as ArrayList<EmployerMessageModel.MessageDataModel>)
*/

                            if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                                Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                                employerMessageListAdapter?.addLoadingFooter()
                            } else {
                                Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                                isLastPages = true
                            }

                        } else {
                            //toast("came here")
                            totalRecords = "0"
                        }

                        Log.d("tot", "total = $totalRecords")
                         val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Messages"
                        messageCountTV.text = Html.fromHtml(styledText)

                        if (totalRecords?.toInt()!! > 1) {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Messages"
                            messageCountTV?.text = Html.fromHtml(styledText)
                        } else if (totalRecords?.toInt()!! <= 1 || totalRecords.toInt()!! == null || totalRecords == "0") {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Messages"
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



}
