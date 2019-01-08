package com.bdjobs.app.editResume.personalInfo.fragments.personalDetails


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.GetPersInfo
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_personal_details_view.*
import kotlinx.android.synthetic.main.layout_perso_header_view.*
import kotlinx.android.synthetic.main.layout_perso_view_1.*
import kotlinx.android.synthetic.main.layout_perso_view_2.*
import kotlinx.android.synthetic.main.layout_perso_view_3.*
import kotlinx.android.synthetic.main.layout_perso_view_4.*
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
        personalInfoCB = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        personalInfoCB.setEditButton(true, "editPersonal")
        personalInfoCB.setTitle(getString(R.string.title_personal))
        doWork()

        d("editResPersView photo:" + session.userPicUrl)
        d("editResPersView name:" + session.fullName)
        d("editResPersView email:" + session.email)
    }

    private fun doWork() {
        nsView.hide()
        shimmerStart()
        populateData()
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getPersonalInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetPersInfo> {
            override fun onFailure(call: Call<GetPersInfo>, t: Throwable) {
                shimmerStop()
                nsView.show()
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<GetPersInfo>, response: Response<GetPersInfo>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        nsView.show()
                        val respo = response.body()
                        personalInfoCB.passPersonalData(respo?.data?.get(0)!!)
                        setupViews(respo)
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    nsView.show()
                    activity.toast("${response.body()?.message}")
                    activity.logException(e)
                }
            }
        })
    }

    private fun setupViews(info: GetPersInfo?) {
        ivUserImage.loadCircularImageFromUrl(session.userPicUrl)
        val a = info?.data?.get(0)?.firstName + " "
        val b = info?.data?.get(0)?.lastName
        val sb = StringBuilder()
        tvName.text = sb.append(a).append(b)
        tvEmail.text = session.email
        tvFname.text = info?.data?.get(0)?.fatherName
        tvMname.text = info?.data?.get(0)?.motherName
        tvDoB.text = info?.data?.get(0)?.dateofBirth
        tvReligion.text = info?.data?.get(0)?.religion
        tvGender.text = info?.data?.get(0)?.gender
        tvMaterialSts.text = info?.data?.get(0)?.maritalStatus
        tvNationality.text = info?.data?.get(0)?.nationality
        tvNatid.text = info?.data?.get(0)?.nationalIdNo
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
