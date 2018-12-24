package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.editResume.adapters.models.ArmydataItem
import com.bdjobs.app.editResume.adapters.models.GetArmyEmpHis
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import kotlinx.android.synthetic.main.fragment_army_emp_his_view.*
import kotlinx.android.synthetic.main.layout_armyempview.*
import kotlinx.android.synthetic.main.layout_armyempview1.*
import kotlinx.android.synthetic.main.layout_armyempview2.*
import kotlinx.android.synthetic.main.layout_armysingleview.*
import kotlinx.android.synthetic.main.layout_armysingleview1.*
import kotlinx.android.synthetic.main.layout_armysingleview2.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArmyEmpHisViewFragment : Fragment() {
    private lateinit var empHisCB: EmpHisCB
    private lateinit var dModel: ArmydataItem
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_army_emp_his_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        empHisCB = activity as EmpHisCB
        empHisCB.setTitle("Employment History (Retired Army Person)")
        empHisCB.setDeleteButton(false)
    }

    override fun onResume() {
        super.onResume()
        doWork()
    }

    private fun doWork() {
        populateData()
        empHisCB.setDeleteButton(false)
        fab_eh_army.setOnClickListener {
            empHisCB.goToEditInfo("army_edit")
        }
    }

    private fun populateData() {
        val call = ApiServiceMyBdjobs.create().getArmyExpsList(session.userId, session.decodId)
        call.enqueue(object : Callback<List<GetArmyEmpHis>> {
            override fun onFailure(call: Call<List<GetArmyEmpHis>>, t: Throwable) {
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<List<GetArmyEmpHis>>, response: Response<List<GetArmyEmpHis>>) {
                try {
                    if (response.isSuccessful) {
                        val respo = response.body()?.get(0)
                        dModel = respo?.armydata?.get(0)!!
                        empHisCB.passArmyData(dModel)
                        tvBa?.text = dModel.baNo1
                        tvBaNo?.text = dModel.baNo2
                        tvRank?.text = dModel.rank
                        tvArmyType?.text = dModel.type
                        tvTrade?.text = dModel.trade
                        tvArms?.text = dModel.arms
                        tvCourse?.text = dModel.course
                        tvCommisDate?.text = dModel.dateOfCommission
                        tvRetireDate?.text = dModel.dateOfRetirement
                    }
                } catch (e: Exception) {
                    activity.error("++${e.message}")
                }
            }
        })
    }

}
