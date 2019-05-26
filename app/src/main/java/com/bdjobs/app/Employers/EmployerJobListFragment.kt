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
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModel
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModelData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_employer_job_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerJobListFragment : Fragment() {
    private var employersJobListsAdapter: EmployerJobListAdapter? = null
    private lateinit var communicator: EmployersCommunicator

    //  private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_job_list, container, false)
        Log.d("oncreateview", "oncreateview")
        //  loadJobList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as EmployersCommunicator
        backIMV?.setOnClickListener {
            communicator?.backButtonPressed()
        }

        suggestiveSearchET?.text = communicator?.getCompanyName()
        loadJobList()
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        adView?.show()

        Log.d("onResume", "onResume")
    }


    private fun loadJobList() {
        employerjobList_RV?.hide()
        favCountTV?.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmerAnimation()
        Log.d("hello", "getCompanyID = ${communicator.getCompanyID()}\ncompanyname = ${communicator.getCompanyName()}\n")
        ApiServiceJobs.create().getEmpJobLists(id = communicator.getCompanyID(), companyname = communicator.getCompanyName(), encoded = Constants.ENCODED_JOBS, jobid = communicator.getJobId()).enqueue(object : Callback<EmployerJobListsModel> {
            override fun onFailure(call: Call<EmployerJobListsModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<EmployerJobListsModel>, response: Response<EmployerJobListsModel>)
            {
                try {
                    var statuscode = response.body()?.statuscode
                    var jobLists = response.body()?.data
                    var totalRecords = response.body()?.data?.size
                    Log.d("callAppliURl", "url: ${call?.request()} and ")

                    //       Toast.makeText(context, statuscode + "---" + jobtitle, Toast.LENGTH_LONG).show()
                    Log.d("hello", jobLists.toString())

                    employersJobListsAdapter = EmployerJobListAdapter(activity!!)
                    employerjobList_RV?.adapter = employersJobListsAdapter
                    employerjobList_RV?.setHasFixedSize(true)
                    employerjobList_RV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    Log.d("initPag", "called")
                    employerjobList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    employersJobListsAdapter?.addAll((jobLists as List<EmployerJobListsModelData>?)!!)

                    /*val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs"
                    favCountTV?.text = Html.fromHtml(styledText)

*/
                    if (totalRecords?.toString()?.isNullOrEmpty()!!){
                        totalRecords = 0
                    }

                    if (totalRecords?.toInt()!! > 1){
                        val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Jobs"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }
                    else {
                        val styledText = "<b><font color='#13A10E'>$totalRecords</font></b> Job"
                        favCountTV?.text = Html.fromHtml(styledText)
                    }

                    employerjobList_RV?.show()
                    favCountTV?.show()
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmerAnimation()
                } catch (e: Exception) {
                    logException(e)
                }


            }

        })
    }


}
