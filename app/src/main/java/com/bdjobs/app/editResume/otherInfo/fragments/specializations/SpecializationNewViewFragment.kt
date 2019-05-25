package com.bdjobs.app.editResume.otherInfo.fragments.specializations

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.SpecializationViewAdapter
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.adapters.models.SpecializationDataModel
import com.bdjobs.app.editResume.adapters.models.SpecialzationModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.skillListView
import kotlinx.android.synthetic.main.fragment_specialization_new_view.*
import kotlinx.android.synthetic.main.fragment_specialization_new_view.curricularTV
import kotlinx.android.synthetic.main.fragment_specialization_new_view.fab_specialization_add
import kotlinx.android.synthetic.main.fragment_specialization_new_view.mainlayout
import kotlinx.android.synthetic.main.fragment_specialization_new_view.skillDescriptionTV
import kotlinx.android.synthetic.main.fragment_view_specialization.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import java.util.*


class SpecializationNewViewFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var specializationViewAdapter: SpecializationViewAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var session: BdjobsUserSession

    private var arr: ArrayList<Skill?>? = null

    private lateinit var eduCB: OtherInfo
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specialization_new_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializaion()

    }


    private fun initializaion(){

        eduCB = activity as OtherInfo
        eduCB.setTitle("Specialization")
        eduCB.setEditButton(true)

        session = BdjobsUserSession(activity)

        populateData()


    }



    private fun populateData() {
        mainlayout?.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getSpecializationInfo(session.userId, session.decodId)
        call.enqueue(object : retrofit2.Callback<SpecialzationModel> {
            override fun onFailure(call: Call<SpecialzationModel>, t: Throwable) {
                try {
                    shimmerStop()
                    activity?.toast("Error occurred")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<SpecialzationModel>, response: Response<SpecialzationModel>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()

                        val respo = response.body()
                        arr = respo?.data!![0]?.skills as ArrayList<Skill?>
                        setData(arr!!, respo.data[0]?.description!!, respo.data[0]?.extracurricular!!, respo.data[0]!!)





                    }
                } catch (e: Exception) {
                  /*  shimmerStop()*/
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        logException(e)
                        d("++${e.message}")
                    }
                }

            }
        })
    }


    private fun setData(array: ArrayList<Skill?>, skillDes: String, curricular: String, response: SpecializationDataModel) {

        if (array.size == 0 && TextUtils.isEmpty(skillDes) && TextUtils.isEmpty(curricular)) {
            mainlayout?.hide()
            fab_specialization_add?.show()
            eduCB.setEditButton(false)
        } else {
            mainlayout.show()
            fab_specialization_add?.hide()
            skillDescriptionTV?.text = skillDes
            curricularTV?.text = curricular

            layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            skillListView?.layoutManager = layoutManager
            specializationViewAdapter = SpecializationViewAdapter(activity, arr!!)
            skillListView.adapter = specializationViewAdapter

            noSkillTVLayout.hide()
            skillListView.show()

            eduCB.setDeleteButton(false)
            eduCB.setEditButton(true)

            d("specialization in view fragment ${response.skills!!.size}")

            eduCB.passSpecializationData(response)

            /* addExpList.add(array)*/

        }

    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_specialization_new?.show()
            shimmer_view_container_specialization?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_specialization_new?.hide()
            shimmer_view_container_specialization?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }




}
