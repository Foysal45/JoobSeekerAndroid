package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.C_DataItem
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
        dataStorage = DataStorage(activity)
        d("onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        d("onResume")
        session = BdjobsUserSession(activity)
        contactCB = activity as PersonalInfo
        contactCB.setTitle(getString(R.string.title_contact))
        if (contactCB.getBackFrom() == "") {
            val respo = contactCB.getContactData()
            setupView(respo)
            contactCB.setEditButton(true, "editContact")
        } else {
            doWork()
        }

    }

    private fun doWork() {
        shimmerStart()
        populateData()
        contactCB.setBackFrom("")
    }

    private fun populateData() {
        rlContactMain.hide()
        val call = ApiServiceMyBdjobs.create().getContactInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetContactInfo> {
            override fun onFailure(call: Call<GetContactInfo>, t: Throwable) {
                shimmerStop()
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetContactInfo>, response: Response<GetContactInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        rlContactMain.show()
                        val respo = response.body()
                        contactCB.passContactData(respo?.data?.get(0)!!)
                        setupView(respo?.data?.get(0)!!)
                        contactCB.setEditButton(true, "editContact")
                    }
                } catch (e: Exception) {
                    logException(e)
                    d("++${e.message}")
                }
            }
        })
    }

    private fun setupView(info: C_DataItem?) {

        contactCB.setThana(info?.presentThana)
        contactCB.setPostOffice(info?.presentPostOffice)
        contactCB.setPmThana(info?.permanentThana)
        contactCB.setPmPostOffice(info?.permanentPostOffice)
        contactCB?.setPresentDistrict(info?.presentDistrict)
        contactCB?.setPermanentDistrict(info?.permanentDistrict)

        var presentAddress = if (info?.presentDistrict.equals("")) "" else info?.presentVillage +
                ", " + dataStorage.getLocationNameByID(info?.presentThana) +
                ", " + dataStorage.getLocationNameByID(info?.presentPostOffice) +
                ", " + dataStorage.getLocationNameByID(info?.presentDistrict)
        //", " + dataStorage.getLocationNameByID(info?.presentCountry)

        val isSameOfPresent = info?.addressType1
        var permanentAddress = when {
            isSameOfPresent == "3" -> "Same as present address"
            info?.permanentDistrict?.trim().equals("") -> ""
            else ->
                info?.permanentVillage +
                        ", " + dataStorage.getLocationNameByID(info?.permanentThana) +
                        ", " + dataStorage.getLocationNameByID(info?.permanentPostOffice) +
                        ", " + dataStorage.getLocationNameByID(info?.permanentDistrict)
            //", " + dataStorage.getLocationNameByID(info?.permanentCountry)
        }

        if (info?.permanentVillage?.trim().equals("") && isSameOfPresent != "3") rl_2.hide() else rl_2.show()
        if (info?.email?.trim().equals("") && info?.alternativeEmail?.trim().equals("")) rl_4.hide() else rl_4.show()
        if (info?.mobile?.trim().equals("") && info?.homePhone?.trim().equals("")
                && info?.officePhone?.trim().equals("")) rl_3.hide() else rl_3.show()

        if (info?.presentInsideOutsideBD == "False") {
            presentAddress = presentAddress.replace(", ,".toRegex(), ",")
            presentAddress = presentAddress.replace("Other,".toRegex(), "")
            tvPresentAddress.text = presentAddress.removeLastComma()
        } else {
            var finalValue = TextUtils.concat(presentAddress.replace(", , , ".toRegex(), ", "), dataStorage.getLocationNameByID(info?.presentCountry))
            finalValue = finalValue.replace(",,".toRegex(), ", ")
            tvPresentAddress.text = finalValue.removeLastComma()
        }
        if (info?.permanentInsideOutsideBD == "False") {
            permanentAddress = permanentAddress.replace(", ,".toRegex(), ",")
            permanentAddress = permanentAddress.replace("Other,".toRegex(), "")
            tvPermanentAddress.text = permanentAddress.removeLastComma()
        } else {
            //val sb = StringBuilder()
            //val finalValue = sb.append("$permanentAddress, ").append(dataStorage.getLocationNameByID(info?.permanentCountry)).replace(",".toRegex(), "")
            var finalValue = TextUtils.concat(permanentAddress.replace(", , , ".toRegex(), ", "), dataStorage.getLocationNameByID(info?.permanentCountry))
            finalValue = finalValue.replace(",,".toRegex(), ",")
            //toast("$finalValue")
            tvPermanentAddress.text = finalValue
        }
        tvMobileNo.text = info?.mobile
        val a = info?.email + "\n"
        val b = info?.alternativeEmail
        val sb = StringBuilder()
        tvEmailAddr.text = sb.append(a).append(b)
        var a1 = info?.mobile
        var b1 = info?.homePhone
        var c1 = info?.officePhone
        var sb1 = StringBuilder()

        if (a1 != "")
            sb1.append(a1+"\n")
        if (b1 != "")
            sb1.append(b1+"\n")
        if (c1 != "")
            sb1.append(c1+"\n")

        Log.d("rakib", "${info?.mobile} ${info?.officePhone} ${info?.homePhone}")

        tvMobileNo.text = sb1
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
