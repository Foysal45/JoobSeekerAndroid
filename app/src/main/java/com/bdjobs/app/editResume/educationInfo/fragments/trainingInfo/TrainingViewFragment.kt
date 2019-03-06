package com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.TrainingInfoAdapter
import com.bdjobs.app.editResume.adapters.models.GetTrainingInfo
import com.bdjobs.app.editResume.adapters.models.Tr_DataItem
import com.bdjobs.app.editResume.callbacks.EduInfo
import kotlinx.android.synthetic.main.fragment_training_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainingViewFragment : Fragment() {

    private lateinit var eduCB: EduInfo
    private var adapter: TrainingInfoAdapter? = null
    private var arr: ArrayList<Tr_DataItem>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
    }

    override fun onResume() {
        super.onResume()
        doWork()
    }

    private fun doWork() {
        populateData()
        rv_tr_view?.behaveYourself(fab_tr_add)
        eduCB.setDeleteButton(false)
        eduCB.setTitle(getString(R.string.title_training))
        fab_tr_add?.setOnClickListener {
            eduCB.goToEditInfo("addTr")
        }
    }

    private fun setupRV(items: ArrayList<Tr_DataItem>) {
        rv_tr_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity.applicationContext)
        rv_tr_view.layoutManager = mLayoutManager
        adapter = TrainingInfoAdapter(items, activity)
        rv_tr_view.itemAnimator = DefaultItemAnimator()
        rv_tr_view.adapter = adapter
    }

    private fun populateData() {
        rv_tr_view.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getTrainingInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<GetTrainingInfo> {
            override fun onFailure(call: Call<GetTrainingInfo>, t: Throwable) {
                shimmerStop()
                rv_tr_view?.show()
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<GetTrainingInfo>, response: Response<GetTrainingInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        rv_tr_view?.show()
                        val respo = response.body()
                        arr = respo?.data as ArrayList<Tr_DataItem>
                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        activity.logException(e)
                        activity.error("++${e.message}")
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList?.hide()
            shimmer_view_container_JobList?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

}
