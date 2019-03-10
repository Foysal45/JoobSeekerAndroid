package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
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
        empHisCB.setTitle(getString(R.string.title_army_emp_his))
        empHisCB.setDeleteButton(false)
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
        fab_eh_army.hide()
        val call = ApiServiceMyBdjobs.create().getArmyExpsList(session.userId, session.decodId)
        call.enqueue(object : Callback<GetArmyEmpHis> {
            override fun onFailure(call: Call<GetArmyEmpHis>, t: Throwable) {
                shimmerStop()
                activity?.toast("Error occurred")
                d(t.message.toString() + "url : ${call.request()}")
            }

            override fun onResponse(call: Call<GetArmyEmpHis>, response: Response<GetArmyEmpHis>) {
                d("url : " + call.request().toString())
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        armyMainCl.show()
                        tv_no_data.hide()
                        fab_eh_army.show()
                        nsArmyEmp.show()
                        val respo = response.body()
                        if (respo?.armydata?.size == 0 || respo?.armydata != null) {
                            dModel = respo.armydata[0]!!
                            armyMainCl.show()
                            noData = false
                            fab_eh_army.setImageResource(R.drawable.ic_edit_white)
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
                            noData = true
                            nsArmyEmp.hide()
                            tv_no_data.text = respo?.message.toString()
                            //tv_no_data.show()
                            fab_eh_army.show()
                            fab_eh_army.setImageResource(R.drawable.ic_add_white)
                        }
                    }
                } catch (e: Exception) {
                    //activity.toast(response.body()?.message.toString())
                    //armyMainCl.invisible()
                    d("++${e.message}")
                }
            }
        })
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList.hide()
            shimmer_view_container_JobList.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
