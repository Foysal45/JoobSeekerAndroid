package com.bdjobs.app.editResume.personalInfo.fragments.personalDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.editResume.adapters.models.GetPersInfo
import com.bdjobs.app.editResume.adapters.models.P_DataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_personal_details_view.*
import kotlinx.android.synthetic.main.layout_perso_header_view.*
import kotlinx.android.synthetic.main.layout_perso_view_1.*
import kotlinx.android.synthetic.main.layout_perso_view_2.*
import kotlinx.android.synthetic.main.layout_perso_view_3.*
import kotlinx.android.synthetic.main.layout_perso_view_4.*
import kotlinx.android.synthetic.main.layout_perso_view_5.*
import kotlinx.android.synthetic.main.layout_perso_view_6.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetailsViewFragment : Fragment() {

    private lateinit var personalInfoCB: PersonalInfo
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_details_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
    }

    override fun onResume() {
        super.onResume()
        personalInfoCB = activity as PersonalInfo
        personalInfoCB.setTitle(getString(R.string.title_personal))
        if (personalInfoCB.getBackFrom() == "") {
            val respo = personalInfoCB.getPersonalData()
            setupViews(respo)
            personalInfoCB.setEditButton(true, "editPersonal")
        } else {
            doWork()
        }

        d("editResPersView photo:" + session.userPicUrl)
        d("editResPersView name:" + session.fullName)
        d("editResPersView email:" + session.email)
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        personalInfoCB.setBackFrom("")
    }

    private fun populateData() {
        clPrefAreaView.hide()
        val call = ApiServiceMyBdjobs.create().getPersonalInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetPersInfo> {
            override fun onFailure(call: Call<GetPersInfo>, t: Throwable) {
                try {
                    shimmerStop()
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<GetPersInfo>, response: Response<GetPersInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clPrefAreaView.show()
                        val respo = response.body()
                        personalInfoCB.passPersonalData(respo?.data?.get(0)!!)
                        setupViews(respo.data.get(0)!!)
                        personalInfoCB.setEditButton(true, "editPersonal")
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    e.printStackTrace()
                    logException(e)
                    d("++${e.message}")
                }
            }
        })
    }

    private fun setupViews(info: P_DataItem?) {
        ivUserImage.loadCircularImageFromUrl(session.userPicUrl)
        val a = info?.firstName + " "
        val b = info?.lastName
        val sb = StringBuilder()
        tvName.text = sb.append(a).append(b)
        tvEmail.text = session.email
        tvFname.text = info?.fatherName
        tvMname.text = info?.motherName
        tvDoB.text = info?.dateofBirth
        tvReligion.text = info?.religion
        tvGender.text = info?.gender
        tvMaterialSts.text = info?.maritalStatus
        tvNationality.text = info?.nationality
        tvNatid.text = info?.nationalIdNo
        tv_passport_number.text = info?.passportNumber
        tv_passport_issue_date.text = info?.passportIssueDate
        tv_blood_group.text = info?.bloodGroup
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList.hide()
            shimmer_view_container_JobList.stopShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
