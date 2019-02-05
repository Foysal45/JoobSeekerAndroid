package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_army_emp_history.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ArmyEmpHistoryEditFragment : Fragment() {

    private lateinit var empHisCB: EmpHisCB
    private lateinit var now: Calendar
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage
    var isEdit = false
    private var hID: String = ""
    private var armyID: String = ""
    private var baID: String = ""
    private var ranksID: String = ""
    private var typeID: String = ""
    private var armsID: String = ""
    private lateinit var v: View

    private val commissionDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }
    private val retireDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_army_emp_history, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        dataStorage = DataStorage(activity)
        empHisCB = activity as EmpHisCB
        now = Calendar.getInstance()
        empHisCB.setTitle(getString(R.string.army_employment_history_title))
        doWork()
        if (isEdit) {
            hID = "13"
            empHisCB.setDeleteButton(true)
            preloadedData()
        } else {
            empHisCB.setDeleteButton(false)
            hID = "-13"
            clearEditText()
        }
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())
        et_ba_no?.addTextChangedListener(TW.CrossIconBehave(et_ba_no))
        et_trade?.addTextChangedListener(TW.CrossIconBehave(et_trade))
        et_course?.addTextChangedListener(TW.CrossIconBehave(et_course))
    }

    private fun doWork() {
        addTextChangedListener(et_ba_type, til_ba)
        addTextChangedListener(et_ba_no, til_ba_no)
        addTextChangedListener(et_ranks, til_ranks)
        addTextChangedListener(et_type, til_type)
        addTextChangedListener(et_arms, til_arms)
        addTextChangedListener(et_commission, til_commission)
        addTextChangedListener(et_retire, til_retire)

        et_ba_type.setOnClickListener { setData(editText = et_ba_type, arrayResource = R.array.army_ba, heading = "Select BA Type", tag = "ba") }
        et_ranks.setOnClickListener { setData(editText = et_ranks, arrayResource = R.array.army_ranks, heading = "Select RANK", tag = "rank") }
        et_type.setOnClickListener { setData(editText = et_type, arrayResource = R.array.army_type, heading = "Select TYPE", tag = "type") }
        et_arms.setOnClickListener { setData(editText = et_arms, arrayResource = R.array.army_arms, heading = "Select ARMS", tag = "arms") }

        et_commission.setOnClickListener { pickDate(activity, now, commissionDateSetListener) }
        et_retire.setOnClickListener { pickDate(activity, now, retireDateSetListener) }
        fab_eh_army.setOnClickListener {
            clArmyEmpHistory.closeKeyboard(activity)
            var validation = 0
            validation = isValidate(et_ba_type, til_ba, et_ba_type, true, validation)
            validation = isValidate(et_ranks, til_ranks, et_ranks, true, validation)
            validation = isValidate(et_ba_no, til_ba_no, et_ba_no, true, validation)
            validation = isValidate(et_type, til_type, et_type, true, validation)
            validation = isValidate(et_arms, til_arms, et_arms, true, validation)
            validation = isValidate(et_commission, til_commission, et_commission, true, validation)
            validation = isValidate(et_retire, til_retire, et_retire, true, validation)
            if (validation == 7) updateData()
        }
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateArmyExpsList(userId = session.userId, decodeId = session.decodId, isResumeUpdate = session.IsResumeUpdate,
                txtBANo = et_ba_no.getString(), comboBANo = et_ba_type.getString(), comboArms = et_arms.getString(), comboRank = et_ranks.getString(), comboType = et_type.getString(),
                txtCourse = et_course.getString(), txtTrade = et_trade.getString(), cboCommissionDate = et_commission.getString(), cboRetirementDate = et_retire.getString(), arm_id = armyID, hId = hID)

        Log.d("armyHis", "${session.userId}, ${session.decodId}, ${session.IsResumeUpdate},\n" +
                "comboBANo = ${et_ba_no.getString()}, txtBANo = $baID, comboArms = $armsID, comboRank = $ranksID, comboType = $typeID,\n" +
                "                txtCourse = ${et_course.getString()}, txtTrade = ${et_trade.getString()}, cboCommissionDate = ${et_commission.getString()}, cboRetirementDate = ${et_retire.getString()}, arm_id = $armyID, hId = $hID")

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            empHisCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })

    }

    private fun updateDateInView(c: Int) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {
            et_commission.setText(sdf.format(now.time))
        } else {
            et_retire.setText(sdf.format(now.time))
        }
    }

    private fun preloadedData() {
        val data = empHisCB.getArmyData()
        armyID = data.armId!!
        et_ba_type.setText(data.baNo1)
        et_ba_no.setText(data.baNo2)
        et_ranks.setText(data.rank)
        et_type.setText(data.type)
        et_arms.setText(data.arms)
        et_course.setText(data.course)
        et_trade.setText(data.trade)
        et_commission.setText(data.dateOfCommission)
        et_retire.setText(data.dateOfRetirement)
    }


    private fun clearEditText() {
        et_ba_type.clear()
        et_ba_no.clear()
        et_ranks.clear()
        et_type.clear()
        et_arms.clear()
        et_course.clear()
        et_trade.clear()
        et_commission.clear()
        et_retire.clear()
        disableError()
    }

    private fun disableError() {
        til_ba.hideError()
        til_ba_no.hideError()
        til_ranks.hideError()
        til_type.hideError()
        til_arms.hideError()
        til_commission.hideError()
        til_retire.hideError()
    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("ArmyPersonalInfo", "555", session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        empHisCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            empHisCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun setData(editText: TextInputEditText, arrayResource: Int, heading: String, tag: String) {
        val list = resources.getStringArray(arrayResource).toList()
        activity.selector(heading, list) { _, i ->
            //activity?.toast("position : $i")
            getPosition(tag, i)
            editText.setText(list[i])
            editText.requestFocus()
        }
    }

    private fun getPosition(tag: String, pos: Int) {
        when (tag) {
            "ba" -> baID = pos.toString()
            "rank" -> ranksID = pos.toString()
            "type" -> typeID = pos.toString()
            "arms" -> armsID = pos.toString()
        }
    }
}
