package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.models.GetContactInfo
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_contact_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactViewFragment : Fragment() {
    private lateinit var contactCB: PersonalInfo
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        contactCB = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        doWork()
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        contactCB.setEditButton(true)
        contactCB.setTitle(getString(R.string.title_contact))
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getContactInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetContactInfo> {
            override fun onFailure(call: Call<GetContactInfo>, t: Throwable) {
                shimmerStop()
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<GetContactInfo>, response: Response<GetContactInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        val respo = response.body()
                        setupView(respo)
                    }
                } catch (e: Exception) {
                    activity.toast("${response.body()?.message}")
                    activity.logException(e)
                }
            }
        })
    }

    private fun setupView(info: GetContactInfo?) {
        tvPresentAddress.text = info?.data?.get(0)?.presentAddress
        tvPermanentAddress.text = info?.data?.get(0)?.permanentAddress
        tvMobileNo.text = info?.data?.get(0)?.presentAddress
        val a = info?.data?.get(0)?.email + "\n"
        val b = info?.data?.get(0)?.alternativeEmail
        val sb = StringBuilder()
        tvEmailAddr.text = sb.append(a).append(b)
        val a1 = info?.data?.get(0)?.mobile + "\n"
        val b1 = info?.data?.get(0)?.homePhone + "\n"
        val c1 = info?.data?.get(0)?.officePhone
        val sb1 = StringBuilder()
        tvMobileNo.text = sb1.append(a1).append(b1).append(c1)
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
