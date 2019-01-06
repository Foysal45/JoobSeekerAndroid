package com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_training_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainingEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hTrainingID: String
    private lateinit var hID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())
        doWork()
        if (isEdit) {
            hID = "2"
            eduCB.setDeleteButton(true)
            preloadedData()
            d("hid val $isEdit : $hID")
        } else {
            eduCB.setDeleteButton(false)
            hID = "-2"
            clearEditText()
            hTrainingID = ""
            d("hid val $isEdit: $hID")
        }
    }

    private fun preloadedData() {
        val data = eduCB.getTrainingData()
        hTrainingID = data.trId.toString()
        etTrTitle.setText(data.title)
        etTrTopic.setText(data.topic)
        etTrCountry.setText(data.country)
        etTrTrainingYear.setText(data.year)
        etTrInstitute.setText(data.institute)
        etTrLoc.setText(data.location)
        etTrDuration.setText(data.duration)
        d("values : ${data.country}")
    }

    private fun doWork() {
        eduCB.setTitle(getString(R.string.title_training))
        fab_tr_update.setOnClickListener { updateData() }
    }


    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateTrainingList(session.userId, session.decodId, session.IsResumeUpdate,
                etTrTitle.getString(), etTrInstitute.getString(), etTrCountry.getString(), etTrTrainingYear.getString(),
                etTrDuration.getString(), hID, etTrTopic.getString(), etTrLoc.getString(), hTrainingID)

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                activity.stopProgressBar(loadingProgressBar)
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    private fun clearEditText() {
        etTrTitle.clear()
        etTrTopic.clear()
        etTrCountry.clear()
        etTrTrainingYear.clear()
        etTrInstitute.clear()
        etTrLoc.clear()
    }

    private fun showHideCrossButton(editText: TextInputEditText) {
        if (editText.text.isNullOrBlank()) {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText.clearTextOnDrawableRightClick()
        }
    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Education", hTrainingID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                }
            }
        })
    }


}
