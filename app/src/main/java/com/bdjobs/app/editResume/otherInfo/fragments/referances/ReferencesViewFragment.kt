package com.bdjobs.app.editResume.otherInfo.fragments.referances


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
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.ReferenceAdapter
import com.bdjobs.app.editResume.adapters.models.ReferenceDataModel
import com.bdjobs.app.editResume.adapters.models.ReferenceModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import kotlinx.android.synthetic.main.fragment_references_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferencesViewFragment : Fragment() {

    private lateinit var eduCB: OtherInfo
    private var adapter: ReferenceAdapter? = null
    private var arr: ArrayList<ReferenceDataModel>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_references_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        doWork()
    }

    private fun doWork() {
        populateData()
        /*   rv_lang_view.behaveYourself(fab_language_add)*/
        eduCB.setDeleteButton(false)
        eduCB.setTitle("References")

        fab_reference_add?.setOnClickListener {

            eduCB.goToEditInfo("addReference")

        }

    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_reference?.show()
            shimmer_view_container_reference?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_reference?.hide()
            shimmer_view_container_reference?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


    private fun setupRV(items: ArrayList<ReferenceDataModel>) {
        rv_reference_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity.applicationContext)
        rv_reference_view.layoutManager = mLayoutManager
        adapter = ReferenceAdapter(items, activity)
        rv_reference_view.itemAnimator = DefaultItemAnimator()
        rv_reference_view.adapter = adapter
    }

    private fun populateData() {
        rv_reference_view?.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getReferenceInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<ReferenceModel> {
            override fun onFailure(call: Call<ReferenceModel>, t: Throwable) {
                try {
                    shimmerStop()
                    rv_reference_view?.show()
                    activity?.toast("Error occurred")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<ReferenceModel>, response: Response<ReferenceModel>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        rv_reference_view.show()
                        val respo = response.body()


                        Log.d("dsfklhgjfd;h", "$respo")

                        arr = respo?.data as ArrayList<ReferenceDataModel>


                        if (arr!!.size == 2) {

                            fab_reference_add.hide()
                        } else {
                            fab_reference_add.show()
                        }

                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    fab_reference_add?.show()
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        logException(e)
                        activity?.error("++${e.message}")
                    }
                }
                adapter?.notifyDataSetChanged()


            }
        })
    }




}