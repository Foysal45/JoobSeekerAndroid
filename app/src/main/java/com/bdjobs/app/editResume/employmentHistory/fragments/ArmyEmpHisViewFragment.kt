package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
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
    private var noData: Boolean = false

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
        shimmerStart()
        populateData()
        empHisCB.setDeleteButton(false)
        fab_eh_army.setOnClickListener {
            if (noData) {
                empHisCB.goToEditInfo("army_add")
            } else {
                empHisCB.goToEditInfo("army_edit")
            }
        }
    }

    private fun populateData() {
        armyMainCl.hide()
        val call = ApiServiceMyBdjobs.create().getArmyExpsList(session.userId, session.decodId)
        call.enqueue(object : Callback<GetArmyEmpHis> {
            override fun onFailure(call: Call<GetArmyEmpHis>, t: Throwable) {
                shimmerStop()
                activity.toast("Error occurred")
                d(t.message.toString() + "url : ${call.request()}")
            }

            override fun onResponse(call: Call<GetArmyEmpHis>, response: Response<GetArmyEmpHis>) {
                d("url : " + call.request().toString())
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        val respo = response.body()
                        dModel = respo?.armydata?.get(0)!!
                        armyMainCl.show()
                        noData = false
                        fab_eh_army.setImageResource(R.drawable.ic_arrow_down)
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
                    } else {
                        d("else resp : " + response.code() + "message: ${response.body()?.message}")
                        activity.toast(response.body()?.message.toString())
                    }
                } catch (e: Exception) {
                    noData = true
                    fab_eh_army.setImageResource(R.drawable.ic_add_white)
                    activity.toast(response.body()?.message.toString())
                    armyMainCl.invisible()
                    activity.error("++${e.message}")
                }
            }
        })
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
