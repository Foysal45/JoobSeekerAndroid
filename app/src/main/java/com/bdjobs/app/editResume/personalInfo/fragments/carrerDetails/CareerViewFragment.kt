package com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.models.GetCarrerInfo
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_career_view.*
import kotlinx.android.synthetic.main.layout_carrer_view_1.*
import kotlinx.android.synthetic.main.layout_carrer_view_2.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CareerViewFragment : Fragment() {

    private lateinit var contactCB: PersonalInfo
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_career_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        contactCB = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        contactCB.setTitle(getString(R.string.title_career))
        doWork()
    }

    private fun doWork() {
        shimmerStart()
        populateData()
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getCareerInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetCarrerInfo> {
            override fun onFailure(call: Call<GetCarrerInfo>, t: Throwable) {
                shimmerStop()
                //clContent.show()
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetCarrerInfo>, response: Response<GetCarrerInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clContent.show()
                        val respo = response.body()
                        val data = respo?.data?.get(0)!!
                        contactCB.setEditButton(true, "editCareer")
                        contactCB.passCareerData(data)
                        setupView(respo)
                    }
                } catch (e: Exception) {
                    clContent.show()
                    if (activity != null) {
                        activity.toast("${response.body()?.message}")
                        activity.logException(e)
                        activity.error("++${e.message}")
                    }
                }
            }
        })
    }

    private fun setupView(info: GetCarrerInfo?) {
        tvObjectives.text = info?.data?.get(0)?.objective
        tvPSal.text = info?.data?.get(0)?.presentSalary
        tvExSal.text = info?.data?.get(0)?.expectedSalary
        tvLookingFor.text = info?.data?.get(0)?.lookingFor
        tvAvailableFor.text = info?.data?.get(0)?.availableFor
    }

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
