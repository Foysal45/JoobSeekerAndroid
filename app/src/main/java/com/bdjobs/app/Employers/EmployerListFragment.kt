package com.bdjobs.app.Employers

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerListModelClass
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.getString
import kotlinx.android.synthetic.main.fragment_employer_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployerListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var orgName = ""
    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    private lateinit var listCommunicator: EmployersCommunicator
    private lateinit var employerListAdapter: EmployerListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_employer_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listCommunicator = activity as EmployersCommunicator
        backIV.setOnClickListener {
            listCommunicator.backButtonPressed()
        }
        initPagination()
    }


    override fun onResume() {
        super.onResume()
        //loadEmployerList(orgType)

        searchBTN.setOnClickListener {
            orgName = suggestiveSearch_ET?.getString()!!
            TOTAL_PAGES =null
            pgNo=PAGE_START
            isLastPages = false
            isLoadings = false
            initPagination()
            Log.d("searchBTN", "searchBTN text: $orgName")
        }
    }

    private fun initPagination() {
        employerListAdapter = EmployerListAdapter(activity!!)
        employerList_RV!!.setHasFixedSize(true)
        employerList_RV!!.adapter = employerListAdapter
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        employerList_RV!!.layoutManager = layoutManager as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        employerList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()


        employerList_RV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage(orgName)
            }
        })

        loadFirstPage(orgName)

    }

    private fun loadFirstPage(orgname: String) {

        ApiServiceJobs.create().getEmpLists(encoded = Constants.ENCODED_JOBS, orgName = orgname, page = pgNo.toString()).enqueue(object : Callback<EmployerListModelClass> {
            override fun onFailure(call: Call<EmployerListModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<EmployerListModelClass>, response: Response<EmployerListModelClass>) {

                Log.d("callAppliURl", "url: ${call?.request()} and $orgname")
                TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()

                if (!response?.body()?.data.isNullOrEmpty()) {
                    employerList_RV!!.visibility = View.VISIBLE
                    employerListAdapter?.removeAll()
                    employerListAdapter?.addAll(response?.body()?.data!!)

                    if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                        Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                        employerListAdapter?.addLoadingFooter()
                    } else {
                        Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                        isLastPages = true
                    }

                }
            }

        })


    }

    private fun loadNextPage(orgname: String) {

        Log.d("pg", pgNo.toString())
        ApiServiceJobs.create().getEmpLists(encoded = Constants.ENCODED_JOBS, orgName = orgname, page = pgNo.toString()).enqueue(object : Callback<EmployerListModelClass> {
            override fun onFailure(call: Call<EmployerListModelClass>, t: Throwable) {
                error("onFailure", t)
            }


            override fun onResponse(call: Call<EmployerListModelClass>, response: Response<EmployerListModelClass>) {

                Log.d("callAppliURl", "url: ${call?.request()} and $pgNo")

                TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()
                employerListAdapter?.removeLoadingFooter()
                isLoadings = false

                employerListAdapter?.addAll(response?.body()?.data!!)


                if (pgNo != TOTAL_PAGES)
                    employerListAdapter?.addLoadingFooter()
                else {
                    isLastPages = true
                }
            }

        })


    }


}
