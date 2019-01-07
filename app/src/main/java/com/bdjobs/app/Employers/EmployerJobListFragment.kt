package com.bdjobs.app.Employers

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import android.app.Fragment
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerJobListsModel
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_employer_job_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EmployerJobListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EmployerJobListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EmployerJobListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var employersJobListsAdapter: EmployerJobListAdapter? = null
    private lateinit var communicator: EmployersCommunicator

    //  private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        backIMV.setOnClickListener {
            communicator.backButtonPressed()
        }


    }

    override fun onResume() {
        super.onResume()
        suggestiveSearchET.text = communicator.getCompanyName()
        loadJobList()

        Log.d("onResume", "onResume")
    }


    private fun loadJobList() {
        employerjobList_RV.hide()
        favCountTV.hide()
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()
        Log.d("hello", "hello")
        ApiServiceJobs.create().getEmpJobLists(id = communicator.getCompanyID(), companyname = communicator.getCompanyName(), encoded = Constants.ENCODED_JOBS, jobid = communicator.getJobId()).enqueue(object : Callback<EmployerJobListsModel> {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFailure(call: Call<EmployerJobListsModel>, t: Throwable) {
                error("onFailure", t)
//                Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()
//                Log.d("hello", "hello")

            }

            override fun onResponse(call: Call<EmployerJobListsModel>, response: Response<EmployerJobListsModel>) {
                var statuscode = response.body()?.statuscode
                var jobLists = response.body()?.data
                var totalRecords = response.body()?.data?.size
                Log.d("callAppliURl", "url: ${call?.request()} and ")

                //       Toast.makeText(context, statuscode + "---" + jobtitle, Toast.LENGTH_LONG).show()
                Log.d("hello", jobLists.toString())

                employersJobListsAdapter = EmployerJobListAdapter(activity!!)
                employerjobList_RV!!.adapter = employersJobListsAdapter
                employerjobList_RV!!.setHasFixedSize(true)
                employerjobList_RV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                Log.d("initPag", "called")
                employerjobList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                employersJobListsAdapter?.addAll(jobLists!!)


                val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Jobs"
                favCountTV.text = Html.fromHtml(styledText)

                employerjobList_RV.show()
                favCountTV.show()
                shimmer_view_container_JobList.hide()
                shimmer_view_container_JobList.stopShimmerAnimation()


            }

        })
    }


}
