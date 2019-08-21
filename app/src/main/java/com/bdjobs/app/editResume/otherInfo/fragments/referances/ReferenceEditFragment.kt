package com.bdjobs.app.editResume.otherInfo.fragments.referances

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.referUpdate
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_reference_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReferenceEditFragment : Fragment() {


    var isEdit: Boolean = false
    private lateinit var eduCB: OtherInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hReferenceID: String
    private lateinit var hID: String
    private var relation = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reference_edit, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        eduCB.setTitle(getString(R.string.title_reference))
        initialization()
        doWork()
        if (!isEdit) {
            eduCB.setDeleteButton(false)
            hID = "-7"
            clearEditText()
            hReferenceID = ""
            d("hid val $isEdit: $hID")
        }


    }


    private fun initialization() {
        addTextChangedListener(etRefName, referenceNameTIL)
        addTextChangedListener(etRefOrganization, refOrganizationTIL)
        addTextChangedListener(etRefDesignation, refDesignationTIL)

        etRefName.addTextChangedListener(TW.CrossIconBehave(etRefName))
        etRefOrganization.addTextChangedListener(TW.CrossIconBehave(etRefOrganization))
        etRefDesignation.addTextChangedListener(TW.CrossIconBehave(etRefDesignation))
        etRFAddress.addTextChangedListener(TW.CrossIconBehave(etRFAddress))
        etRefPhoneOffice.addTextChangedListener(TW.CrossIconBehave(etRefPhoneOffice))
        etPhoneRes.addTextChangedListener(TW.CrossIconBehave(etPhoneRes))
        etRefMobile.addTextChangedListener(TW.CrossIconBehave(etRefMobile))
        etRfEmail.addTextChangedListener(TW.CrossIconBehave(etRfEmail))

        etRfEmail.easyOnTextChangedListener { charSequence ->
            if (emailValidityCheck(charSequence.toString())) {
                rfEmailTIL.hideError()

            }


        }


    }


    private fun emailValidityCheck(email: String): Boolean {

        when {
            !isValidEmail(email) -> {
                rfEmailTIL?.showError(getString(R.string.email_invalid_message))
                requestFocus(etRfEmail)
                return false
            }
            else -> {
                rfEmailTIL?.hideError()
                return true
            }
        }


    }


    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    fun dataDelete() {
        activity?.showProgressBar(referenceLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Reference", hReferenceID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(referenceLoadingProgressBar)
                        val resp = response.body()
                        activity?.toast(resp?.message.toString())
                        clearEditText()
                        eduCB.setBackFrom(referUpdate)
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    //activity.stopProgressBar(referenceLoadingProgressBar)
                    //activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun clearEditText() {
        etRefName?.clear()
        etRefOrganization?.clear()
        etRefDesignation?.clear()
        etRFAddress?.clear()
        etPhoneRes?.clear()
        etRefPhoneOffice?.clear()
        etRefMobile?.clear()
        etRfEmail?.clear()
        disableError()
        relation = ""

    }


    private fun disableError() {
        referenceNameTIL?.hideError()
        refOrganizationTIL?.hideError()
        refDesignationTIL?.hideError()
        reAddressTIL?.hideError()
        rfPhoneResTIL?.hideError()
        trPhoneOfficeTIL?.hideError()
        refMobileTIL?.hideError()
        rfEmailTIL?.hideError()
        etRfEmail?.clearFocus()

    }


    private fun doWork() {

        getDataFromChip()
        fab_reference_update?.setOnClickListener {

            var validation = 0
            validation = isValidate(etRefName, referenceNameTIL, etRefOrganization, true, validation)
            validation = isValidate(etRefOrganization, refOrganizationTIL, etRefDesignation, true, validation)
            validation = isValidate(etRefDesignation, refDesignationTIL, etRefDesignation, true, validation)


            if (validation == 3) {
                if (relation.equalIgnoreCase("")) {
                    activity.toast("Relation cannot be empty")

                } else {
                    validation++
                    if (validation == 4) {

                        if (!etRfEmail.getString().isNullOrEmpty()) {

                            if (emailValidityCheck(etRfEmail.getString())) {
                                updateData()
                            }


                        } else {

                            updateData()
                        }


                    }
                }
            }
        }
    }


    private fun updateData() {
        activity?.showProgressBar(referenceLoadingProgressBar)

        Log.d("fklfh", "hID $hID, relation $relation, hReferenceID $hReferenceID ")


        val call = ApiServiceMyBdjobs.create().updateReferenceList(session.userId, session.decodId, etRefName.getString(),
                etRefOrganization.getString(), etRefDesignation.getString(), hID, session.IsResumeUpdate, etRFAddress.getString(),
                etRefPhoneOffice.getString(), etPhoneRes.getString(), etRefMobile.getString(), etRfEmail.getString(), relation, hReferenceID)

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(referenceLoadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {

                try {
                    activity?.stopProgressBar(referenceLoadingProgressBar)
                    if (response.isSuccessful) {
                        activity.stopProgressBar(referenceLoadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())

                        Log.d("fklfh", "$resp")

                        if (resp?.statuscode == "4") {
                            eduCB.setBackFrom(referUpdate)
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    /*   assert(activity != null)*/
                    /*  activity.stopProgressBar(referenceLoadingProgressBar)*/
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }


    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())

        if (isEdit) {
            eduCB.setDeleteButton(true)
            preloadedData()
            hID = "1"

            d("hid val $isEdit : $hID")
        } else {
            eduCB.setDeleteButton(false)
            hID = "-7"
            clearEditText()
            d("hid val $isEdit: $hID")
        }
    }


    private fun getDataFromChip() {
        getDataFromChipGroup(cgRelation)
    }


    private fun preloadedData() {


        val data = eduCB.getReferenceData()
        hReferenceID = data.refId.toString()
        selectChip(cgRelation, data.relation!!)
        etRefName?.setText(data.name)
        etRefOrganization?.setText(data.organization)
        etRefDesignation?.setText(data.designation)
        etRFAddress?.setText(data.address)
        etPhoneRes?.setText(data.phoneRes)
        etRefPhoneOffice?.setText(data.phoneOffice)
        etRefMobile?.setText(data.mobile)
        etRfEmail?.setText(data.email)
        disableError()
        /* d("values : ${data.country}")*/
    }


    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
            }
        }
    }


    private fun getDataFromChipGroup(cg: ChipGroup) {

        Log.d("djkgnhdg", "cg $cg")
        cg.setOnCheckedChangeListener { chipGroup, i ->
            Log.d("djkgnhdg", " i $i")

            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgRelation -> {
                        val chips = when (data) {
                            "Relative" -> "Relative"
                            "Family Friend" -> "Family Friend"
                            "Academic" -> "Academic"
                            "Professional" -> "Professional"
                            "Other" -> "Other"
                            else -> "O"
                        }
                        debug("value : $chips")
                        relation = chips
                    }


                }
            } else {

                Log.d("djkgnhdg", " else")

                when (chipGroup.id) {
                    R.id.cgRelation -> {
                        relation = ""
                    }

                }
            }


        }


    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


}
