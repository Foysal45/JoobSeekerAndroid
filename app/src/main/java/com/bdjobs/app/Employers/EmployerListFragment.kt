package com.bdjobs.app.Employers

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.EmployerListModelClass

import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.getString
import com.google.android.gms.common.api.Api
import kotlinx.android.synthetic.main.fragment_employer_job_list.*
import kotlinx.android.synthetic.main.fragment_employer_list.*
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
 * [EmployerList.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EmployerList.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EmployerListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var orgType : String = ""

    private lateinit var listCommunicator: EmployersCommunicator
    private lateinit var employerListAdapter: EmployerListAdapter
    private var keyword = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listCommunicator = activity as EmployersCommunicator
        backIV.setOnClickListener {
            listCommunicator.backButtonPressed()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_list, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        loadEmployerList(orgType)
        searchIMGV.setOnClickListener {

            orgType = suggestiveSearch_ET?.getString()!!
            loadEmployerList(orgType)

            Log.d("hhh", orgType)
          //  Toast.makeText(context,orgType, Toast.LENGTH_LONG).show()

        }
    }

    private fun loadEmployerList(orgtype : String){
        ApiServiceJobs.create().getEmpLists(encoded = Constants.ENCODED_JOBS, orgType = orgType).enqueue(object : Callback<EmployerListModelClass>{
            override fun onFailure(call: Call<EmployerListModelClass>, t: Throwable) {
                error("onFailure", t)
//                Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()
//                Log.d("hello", "hello")
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<EmployerListModelClass>, response: Response<EmployerListModelClass>) {


                var statuscode = response.body()?.statuscode
                var empLists = response.body()?.data

                employerListAdapter = EmployerListAdapter(activity!!)
                employerList_RV!!.adapter = employerListAdapter
                employerList_RV!!.setHasFixedSize(true)
                employerList_RV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                Log.d("initPag", "called")
                employerList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                Log.d("hello", empLists.toString())
                employerListAdapter?.addAll(empLists!!)

                employerListAdapter?.notifyDataSetChanged()


                  }

        })

    }


}
