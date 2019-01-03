package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.EmpHistoryAdapter
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.adapters.models.GetExps
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.fragment_emp_history_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmpHistoryViewFragment : Fragment() {
    private lateinit var empHisCB: EmpHisCB
    private var adapter: EmpHistoryAdapter? = null
    private var arr: ArrayList<DataItem>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emp_history_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        empHisCB = activity as EmpHisCB
    }

    override fun onResume() {
        super.onResume()
        doWork()
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        empHisCB.setDeleteButton(false)
        fab_eh_add.setOnClickListener {
            empHisCB.goToEditInfo("add")
        }
    }

    private fun setupRV(items: ArrayList<DataItem>) {
        rv_eh_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        rv_eh_view.layoutManager = mLayoutManager
        adapter = EmpHistoryAdapter(items, activity)
        rv_eh_view.itemAnimator = DefaultItemAnimator()
        rv_eh_view.adapter = adapter
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getExpsList(session.userId, session.decodId)
        call.enqueue(object : Callback<GetExps> {
            override fun onFailure(call: Call<GetExps>, t: Throwable) {
                shimmerStop()
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<GetExps>, response: Response<GetExps>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        val respo = response.body()
                        Log.d("empTest", "${respo?.message}")
                        Log.d("empTest1", "${respo?.toString()}")

                        //activity.toast("${respo?.message}")
                        arr = respo?.data as ArrayList<DataItem>
                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            //adapter?.addAll(arr!!)
                            setupRV(arr!!)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                } catch (e: Exception) {
                    activity.toast("${response.body()?.message}")
                    activity.logException(e)
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun shimmerStart() {
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()
    }

    private fun shimmerStop() {
        shimmer_view_container_JobList.hide()
        shimmer_view_container_JobList.stopShimmerAnimation()
    }
}
