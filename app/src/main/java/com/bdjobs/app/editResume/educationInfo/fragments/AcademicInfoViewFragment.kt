package com.bdjobs.app.editResume.educationInfo.fragments


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
import com.facebook.FacebookSdk
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
        eduCB.setTitle("Academic Qualification")
        fab_aca_add.setOnClickListener {
            eduCB.goToEditInfo("add")
        }
    }

    private fun setupRV(items: ArrayList<AcaDataItem>) {
        rv_aca_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        rv_aca_view.layoutManager = mLayoutManager
        adapter = AcademicInfoAdapter(items, activity)
        rv_aca_view.itemAnimator = DefaultItemAnimator()
        rv_aca_view.adapter = adapter
    }

    private fun populateData() {
        loader_aca.show()
        val call = ApiServiceMyBdjobs.create().getAcaInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<List<GetAcademicInfo>> {
            override fun onFailure(call: Call<List<GetAcademicInfo>>, t: Throwable) {
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<List<GetAcademicInfo>>, response: Response<List<GetAcademicInfo>>) {
                try {
                    if (response.isSuccessful) {
                        loader_aca.hide()
                        val respo = response.body()?.get(0)
                        arr = respo?.data as ArrayList<AcaDataItem>
                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    loader_aca.hide()
                    activity.error("++${e.message}")
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }
}
