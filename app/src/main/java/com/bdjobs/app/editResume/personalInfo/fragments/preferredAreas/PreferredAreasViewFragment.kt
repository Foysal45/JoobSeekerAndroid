package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_preferred_areas_view.*

class PreferredAreasViewFragment : Fragment() {

    private lateinit var prefCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferred_areas_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        prefCallBack = activity as PersonalInfo
        dataStorage = DataStorage(activity)
        doWork()
    }

    private fun doWork() {
        //clORIMainLayout.hide()
        shimmerStart()
        //populateData()
    }


    /*private fun populateData() {

        val call = ApiServiceMyBdjobs.create().getORIInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetORIResponse> {
            override fun onFailure(call: Call<GetORIResponse>, t: Throwable) {
                shimmerStop()
                clORIMainLayout.hide()
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetORIResponse>, response: Response<GetORIResponse>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clORIMainLayout.show()
                        val respo = response.body()
                        oriCallBack.setEditButton(true, "editORI")
                        oriCallBack.passOriData(respo?.data?.get(0)!!)
                        setupView(respo)
                    }
                } catch (e: Exception) {
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        activity.logException(e)
                        activity.error("++${e.message}")
                    }
                }
            }
        })
    }*/

    private fun shimmerStart() {
        try {
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList.hide()
            shimmer_view_container_JobList.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
