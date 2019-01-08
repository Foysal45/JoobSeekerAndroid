package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
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
    private lateinit var dataStorage: DataStorage
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        contactCB = activity as PersonalInfo
        dataStorage = DataStorage(activity)
        d("onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        d("onResume")
        contactCB.setTitle(getString(R.string.title_contact))
        contactCB.setEditButton(true, "editContact")
        doWork()
    }

    private fun doWork() {
        shimmerStart()
        populateData()
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getContactInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetContactInfo> {
            override fun onFailure(call: Call<GetContactInfo>, t: Throwable) {
                shimmerStop()
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetContactInfo>, response: Response<GetContactInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        val respo = response.body()
                        contactCB.passContactData(respo?.data?.get(0)!!)
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
        val presentAddress = info?.data?.get(0)?.presentVillage +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.presentThana) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.presentPostOffice) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.presentDistrict) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.presentCountry)
        val permanentAddress = info?.data?.get(0)?.presentVillage +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.permanentThana) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.permanentPostOffice) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.permanentDistrict) +
                ", " + dataStorage.getLocationNameByID(info?.data?.get(0)?.permanentCountry)
        tvPresentAddress.text = presentAddress
        tvPermanentAddress.text = permanentAddress
        tvMobileNo.text = info?.data?.get(0)?.mobile
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
