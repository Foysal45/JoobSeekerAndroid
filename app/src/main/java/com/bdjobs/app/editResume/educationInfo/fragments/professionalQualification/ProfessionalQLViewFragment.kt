package com.bdjobs.app.editResume.educationInfo.fragments.professionalQualification

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.ProfessionalQFAdapter
import com.bdjobs.app.editResume.adapters.models.ProfessionalDataModel
import com.bdjobs.app.editResume.adapters.models.ProfessionalModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import kotlinx.android.synthetic.main.fragment_professional_ql_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfessionalQLViewFragment : Fragment() {

    private lateinit var eduCB: EduInfo
    private var adapter: ProfessionalQFAdapter? = null
    private var arr: ArrayList<ProfessionalDataModel>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_professional_ql_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        doWork()
    }

    private fun doWork() {
        populateData()
        rv_professional_view?.behaveYourself(fab_professional_add)
        eduCB.setDeleteButton(false)
        eduCB.setTitle(getString(R.string.title_professional_qualification))
        fab_professional_add?.setOnClickListener {
            eduCB.goToEditInfo("addProfessional")
        }
    }


    override fun onResume() {
        super.onResume()
        doWork()
    }


    private fun setupRV(items: ArrayList<ProfessionalDataModel>) {
        rv_professional_view?.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        rv_professional_view?.layoutManager = mLayoutManager
        adapter = ProfessionalQFAdapter(items, activity)
        rv_professional_view?.itemAnimator = DefaultItemAnimator()
        rv_professional_view?.adapter = adapter
    }

    private fun populateData() {
        rv_professional_view?.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getProfessionalInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<ProfessionalModel> {
            override fun onFailure(call: Call<ProfessionalModel>, t: Throwable) {
                shimmerStop()
                rv_professional_view.show()
                activity?.toast("Error occurred")
            }

            override fun onResponse(call: Call<ProfessionalModel>, response: Response<ProfessionalModel>) {
                try {
                if (response.isSuccessful) {
                    shimmerStop()
                    rv_professional_view?.show()
                    val respo = response.body()


                    Log.d("dsfklhgjfd;h", "$respo")
                    arr = respo?.data as ArrayList<ProfessionalDataModel>
                    //activity.toast("${arr?.size}")
                    if (arr != null) {
                        setupRV(arr!!)
                    }
                }
                } catch (e: Exception) {
                      shimmerStop()
                      if (activity != null) {
                          //activity.toast("${response.body()?.message}")
                          activity?.logException(e)
                          activity?.error("++${e.message}")
                      }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_Prf_List?.show()
            shimmer_view_container_Prf_List?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_Prf_List?.hide()
            shimmer_view_container_Prf_List?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


}
