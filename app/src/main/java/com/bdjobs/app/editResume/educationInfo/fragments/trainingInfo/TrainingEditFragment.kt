package com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.clear
import com.bdjobs.app.Utilities.clearTextOnDrawableRightClick
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_training_edit.*

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

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
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
        val data = eduCB.getTrainingData()
        hTrainingID = data.trId.toString()
        etTrTitle.setText(data.title)
        etTrTopic.setText(data.title)
        etTrCountry.setText(data.title)
        etTrTrainingYear.setText(data.title)
        etTrInstitute.setText(data.title)
        etTrLoc.setText(data.title)
    }

    private fun doWork() {
        eduCB.setTitle(getString(R.string.title_training))
        fab_tr_update.setOnClickListener { updateData() }
    }

    private fun updateData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

}
