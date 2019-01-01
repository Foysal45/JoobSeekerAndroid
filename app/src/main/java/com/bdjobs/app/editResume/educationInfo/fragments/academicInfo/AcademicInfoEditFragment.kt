package com.bdjobs.app.editResume.educationInfo.fragments.academicInfo


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.clear
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.showProgressBar
import com.bdjobs.app.Utilities.stopProgressBar
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import kotlinx.android.synthetic.main.fragment_academic_info_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AcademicInfoEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var v: View
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hacaID: String
    private lateinit var hID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_academic_info_edit, container, false)
        return v
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
            hID = "1"
            eduCB.setDeleteButton(true)
            preloadedData()
        } else {
            eduCB.setDeleteButton(false)
            hID = "-1"
            clearEditText()
        }
    }

    private fun preloadedData() {
        val data = eduCB.getData()
        hacaID = data.acId.toString()
        etLevelEdu.setText(data.levelofEducation)
        etExamTitle.setText(data.examDegreeTitle)
        etMajor.setText(data.concentrationMajorGroup)
        etInstitute.setText(data.instituteName)
        etResults.setText(data.result)
        etCGPA.setText(data.marks)
        etScale.setText(data.scale)
        etPassignYear.setText(data.yearofPAssing)
        etDuration.setText(data.duration)
        etAchievement.setText(data.acievement)
        //cbForInstitute.isChecked = data.
        //cbResHide.isChecked = data.
    }

    private fun doWork() {
        eduCB.setTitle(getString(R.string.title_academic))
        fab_aca_edit.setOnClickListener { updateData() }
    }

    private fun updateData() {

    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Education", hacaID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
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

    private fun clearEditText() {
        etLevelEdu.clear()
        etExamTitle.clear()
        etMajor.clear()
        etInstitute.clear()
        etResults.clear()
        etCGPA.clear()
        etScale.clear()
        etPassignYear.clear()
        etDuration.clear()
        etAchievement.clear()
    }
}
