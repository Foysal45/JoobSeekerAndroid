package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_army_emp_history.*
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
    }

    private fun doWork() {
        empHisCB.setTitle(getString(R.string.army_employment_history_title))
        /*et_ba_type.setOnClickListener {
            val divisionList: Array<String> = dataStorage.

            selector("বিভাগ নির্বাচন করুন", divisionList.toList()) { dialogInterface, i ->

                bcDivisionTIET.setText(divisionList[i])
                bcDistrictTIL.requestFocus()

            }
        }*/
        et_commission.setOnClickListener { pickDate(activity, now, commissionDateSetListener) }
        et_retire.setOnClickListener { pickDate(activity, now, retireDateSetListener) }
        fab_eh_army.setOnClickListener {
            clArmyEmpHistory.closeKeyboard(activity)
            updateData()
        }
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateArmyExpsList(session.userId, session.decodId, session.IsResumeUpdate,
                et_ba_no.getString(), et_ba_type.getString(), et_arms.getString(), et_type.getString(),
                et_course.getString(), et_trade.getString(), et_commission.getString(), et_retire.getString(), armyID, hID)
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
                        empHisCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })

    }

    private fun updateDateInView(c: Int) {
        val myFormat = "MM/dd/yyyy" // mention the format you need
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
}
