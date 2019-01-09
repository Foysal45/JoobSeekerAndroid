package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmplyerViewMyResume
import com.bdjobs.app.API.ModelClasses.EmplyerViewMyResumeData
import com.bdjobs.app.Jobs.PaginationScrollListener

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_employer_viewed_my_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class EmployerViewedMyResumeFragment : Fragment() {

    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private lateinit var employerViewedMyResumeAdapter: EmployerViewedMyResumeAdapter
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var employerCommunicator: EmployersCommunicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_viewed_my_resume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employerCommunicator = activity as EmployersCommunicator
        backIMV.setOnClickListener {
            employerCommunicator.backButtonPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        initializeViews()

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
                loadNextPage()
            }
        })

        loadFirstPage()

    }

    private fun loadFirstPage() {

        viewedMyResumeRV.hide()
        favCountTV.hide()
        shimmer_view_container_employerViewedMyList.show()
        shimmer_view_container_employerViewedMyList.startShimmerAnimation()



        ApiServiceMyBdjobs.create().getEmpVwdMyResume(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "10",
                isActivityDate = "1",
                AppsDate = ""


        ).enqueue(object : Callback<EmplyerViewMyResume> {
            override fun onFailure(call: Call<EmplyerViewMyResume>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<EmplyerViewMyResume>, response: Response<EmplyerViewMyResume>) {
                try {
                    Log.d("callAppliURl", "url: ${call?.request()} and ")
                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    var totalRecords = response.body()?.common?.totalNumberOfItems
                    if (!response?.body()?.data.isNullOrEmpty()) {
                        viewedMyResumeRV!!.visibility = View.VISIBLE
                        var value = response.body()?.data
                        employerViewedMyResumeAdapter?.removeAll()
                        employerViewedMyResumeAdapter?.addAll(value as List<EmplyerViewMyResumeData>)

                        if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                            Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                            employerViewedMyResumeAdapter?.addLoadingFooter()
                        } else {
                            Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                            isLastPages = true
                        }


                        if (totalRecords?.toInt()!! > 1) {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Employers viewed my Resume"
                            favCountTV?.text = Html.fromHtml(styledText)
                        } else if (totalRecords?.toInt()!! <= 1 || (totalRecords.toInt()!! == null)) {
                            val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Employer viewed my Resume"
                            favCountTV?.text = Html.fromHtml(styledText)
                        }


                    }

                    viewedMyResumeRV?.show()
                    favCountTV?.show()
                    shimmer_view_container_employerViewedMyList?.hide()
                    shimmer_view_container_employerViewedMyList?.stopShimmerAnimation()

                } catch (exception: Exception) {

                }

            }

        })


    }

    private fun loadNextPage() {
        ApiServiceMyBdjobs.create().getEmpVwdMyResume(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "10",
                isActivityDate = "1",
                AppsDate = ""


        ).enqueue(object : Callback<EmplyerViewMyResume> {
            override fun onFailure(call: Call<EmplyerViewMyResume>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<EmplyerViewMyResume>, response: Response<EmplyerViewMyResume>) {
                try {
                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    employerViewedMyResumeAdapter?.removeLoadingFooter()
                    isLoadings = false

                    employerViewedMyResumeAdapter?.addAll(response?.body()?.data as List<EmplyerViewMyResumeData>)


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


    }

}
