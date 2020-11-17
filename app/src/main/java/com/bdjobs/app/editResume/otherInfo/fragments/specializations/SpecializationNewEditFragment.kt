package com.bdjobs.app.editResume.otherInfo.fragments.specializations


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SpecializationNewEditFragment : Fragment() {

    private lateinit var dataStorage: DataStorage
    private lateinit var levelList: Array<String>
    private lateinit var eduCB: OtherInfo
    private lateinit var session: BdjobsUserSession
    var isEdit: Boolean = false


    override fun onResume() {
        super.onResume()

        if (isEdit) {
            eduCB.setDeleteButton(true)
            eduCB.setEditButton(false)

            //fdghfjkh

        } else {
            eduCB.setEditButton(false)
            eduCB.setDeleteButton(false)


        }


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_specialization_new_edit, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onclick()

    }

    private fun initialization() {

        dataStorage = DataStorage(activity!!)
        levelList = arrayOf(
                "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
                "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )

        eduCB = activity as OtherInfo
        session = BdjobsUserSession(activity)


    }

    private fun onclick() {

        if (isEdit) {
            preloadedData()

        }


        new_fab_specialization_update?.onClick {

            updateData()


        }


    }


    private fun updateData() {
        activity?.showProgressBar(newSpecializationLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateExtraCuriculam(session.userId, session.decodId,
                session.IsResumeUpdate, etSkillDescription.getString(), etCaricular.getString())
        call?.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(newSpecializationLoadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(newSpecializationLoadingProgressBar)
                        val resp = response.body()

                        if (resp?.statuscode == "4") {


                            if (etSkillDescription.getString().isNotEmpty() && etCaricular.getString().isNotEmpty() ){
                                activity?.toast("The information has been updated successfully")

                            } else if (etSkillDescription.getString().isNotEmpty() || etCaricular.getString().isNotEmpty()){
                                activity?.toast("The information has been updated successfully")

                            }

                            eduCB.setBackFrom(Constants.specUpdate)
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    /*  activity.stopProgressBar(specializationLoadingProgressBar)*/
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }


    private fun preloadedData() {

        val data = eduCB.getSpecializationData()
        etSkillDescription.setText(data.description)
        etCaricular.setText(data.extracurricular)


    }


}
