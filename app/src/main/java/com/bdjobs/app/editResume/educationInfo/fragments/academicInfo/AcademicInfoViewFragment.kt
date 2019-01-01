package com.bdjobs.app.editResume.educationInfo.fragments.academicInfo


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
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.AcademicInfoAdapter
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.adapters.models.GetAcademicInfo
import com.bdjobs.app.editResume.callbacks.EduInfo
import kotlinx.android.synthetic.main.fragment_academic_info_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcademicInfoViewFragment : Fragment() {

    private lateinit var eduCB: EduInfo
    private var adapter: AcademicInfoAdapter? = null
    private var arr: ArrayList<AcaDataItem>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_academic_info_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
    }

    override fun onResume() {
        super.onResume()
        doWork()
        Log.d("acaFrag", "calling...")
    }

    private fun doWork() {
        populateData()
        eduCB.setDeleteButton(false)
        eduCB.setTitle(getString(R.string.title_academic))
        fab_aca_add.setOnClickListener {
            eduCB.goToEditInfo("add")
        }
    }

    private fun setupRV(items: ArrayList<AcaDataItem>) {
        rv_aca_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity.applicationContext)
        rv_aca_view.layoutManager = mLayoutManager
        adapter = AcademicInfoAdapter(items, activity)
        rv_aca_view.itemAnimator = DefaultItemAnimator()
        rv_aca_view.adapter = adapter
    }

    private fun populateData() {
        rv_aca_view.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getAcaInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<GetAcademicInfo> {
            override fun onFailure(call: Call<GetAcademicInfo>, t: Throwable) {
                shimmerStop()
                rv_aca_view.show()
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<GetAcademicInfo>, response: Response<GetAcademicInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        rv_aca_view.show()
                        val respo = response.body()
                        arr = respo?.data as ArrayList<AcaDataItem>
                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    rv_aca_view.show()
                    activity.error("++${e.message}")
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
