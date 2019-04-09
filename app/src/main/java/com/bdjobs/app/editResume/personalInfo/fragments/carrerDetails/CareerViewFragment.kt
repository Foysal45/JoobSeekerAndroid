package com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.models.Ca_DataItem
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

    private lateinit var careerInfoCB: PersonalInfo
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_career_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
    }

    override fun onResume() {
        super.onResume()
        careerInfoCB = activity as PersonalInfo
        careerInfoCB.setTitle(getString(R.string.title_career))
        if (careerInfoCB.getBackFrom() == "") {
            val respo = careerInfoCB.getCareerData()
            setupView(respo)
            careerInfoCB.setEditButton(true, "editCareer")
        } else {
            doWork()
        }
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        careerInfoCB.setBackFrom("")
    }

    private fun populateData() {
        clContent.hide()
        val call = ApiServiceMyBdjobs.create().getCareerInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetCarrerInfo> {
            override fun onFailure(call: Call<GetCarrerInfo>, t: Throwable) {
                shimmerStop()
                //clContent.show()
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetCarrerInfo>, response: Response<GetCarrerInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clContent.show()
                        val respo = response.body()
                        val data = respo?.data?.get(0)!!
                        careerInfoCB.passCareerData(data)
                        setupView(respo?.data?.get(0)!!)
                        careerInfoCB.setEditButton(true, "editCareer")
                    }
                } catch (e: Exception) {
                    logException(e)
                    d("++${e.message}")
                }
            }
        })
    }

    private fun setupView(info: Ca_DataItem?) {
        tvObjectives.text = info?.objective
        tvPSal.text = info?.presentSalary
        tvExSal.text = info?.expectedSalary
        tvLookingFor.text = info?.lookingFor
        tvAvailableFor.text = info?.availableFor
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
